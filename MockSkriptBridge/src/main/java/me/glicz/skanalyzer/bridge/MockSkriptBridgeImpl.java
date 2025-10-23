package me.glicz.skanalyzer.bridge;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.hooks.VaultHook;
import ch.njol.skript.hooks.regions.RegionsPlugin;
import me.glicz.skanalyzer.bridge.log.CachingLogHandler;
import me.glicz.skanalyzer.bridge.util.AnalyzeUtils;
import me.glicz.skanalyzer.bridge.util.ScriptUtils;
import me.glicz.skanalyzer.config.Config;
import me.glicz.skanalyzer.result.AnalyzeResults;
import org.skriptlang.skript.lang.script.Script;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static me.glicz.skanalyzer.bridge.util.ScriptUtils.listScripts;
import static me.glicz.skanalyzer.bridge.util.SetUtils.transformSet;

public class MockSkriptBridgeImpl implements SkriptBridge {
    @Override
    public void forceLoadHooks(Config.ForcedHooks forcedHooks) throws IOException {
        if (forcedHooks.vault) {
            String basePackage = VaultHook.class.getPackage().getName();
            Skript.getAddonInstance().loadClasses(basePackage);
        }

        if (forcedHooks.regions) {
            String basePackage = RegionsPlugin.class.getPackage().getName();
            Skript.getAddonInstance().loadClasses(basePackage);
        }
    }

    @Override
    public CompletableFuture<AnalyzeResults> loadScripts(Set<File> files, boolean validateFiles) {
        if (validateFiles) {
            for (File file : files) {
                if (file.isDirectory() || ScriptUtils.isValidFile(file)) {
                    continue;
                }

                return CompletableFuture.failedFuture(new IOException("Invalid file: " + file));
            }
        }

        Set<File> scripts;
        try {
            scripts = listScripts(files, validateFiles);
        } catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }

        // unload already loaded scripts in this path
        ScriptLoader.unloadScripts(transformSet(
                scripts, ScriptLoader::getScript, Objects::nonNull
        ));

        try (CachingLogHandler logHandler = new CachingLogHandler().start()) {
            return ScriptLoader.loadScripts(scripts, logHandler).thenApply(info ->
                    AnalyzeUtils.collectResults(scripts, logHandler)
            );
        }
    }

    @Override
    public boolean unloadScripts(File... files) throws IOException {
        Set<Script> scripts = transformSet(
                listScripts(Set.of(files), false), ScriptLoader::getScript, Objects::nonNull
        );

        if (scripts.isEmpty()) {
            return false;
        }

        ScriptLoader.unloadScripts(scripts);
        return true;
    }

    @Override
    public void unloadAllScripts() {
        ScriptLoader.unloadScripts(ScriptLoader.getLoadedScripts());
    }
}
