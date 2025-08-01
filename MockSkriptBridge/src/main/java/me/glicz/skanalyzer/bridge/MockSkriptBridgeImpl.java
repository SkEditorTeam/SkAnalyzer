package me.glicz.skanalyzer.bridge;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.hooks.VaultHook;
import ch.njol.skript.hooks.regions.RegionsPlugin;
import me.glicz.skanalyzer.AnalyzerHookType;
import me.glicz.skanalyzer.bridge.log.CachingLogHandler;
import me.glicz.skanalyzer.bridge.util.ScriptUtils;
import me.glicz.skanalyzer.result.AnalyzeResult;
import me.glicz.skanalyzer.result.AnalyzeResults;
import org.skriptlang.skript.lang.script.Script;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static java.util.Collections.emptySet;
import static me.glicz.skanalyzer.bridge.util.AnalyzeUtils.toScriptStructure;
import static me.glicz.skanalyzer.bridge.util.ObjectUtils.transformValue;
import static me.glicz.skanalyzer.bridge.util.SetUtils.transformSet;

public class MockSkriptBridgeImpl extends MockSkriptBridge {
    @Override
    public void forceLoadHook(AnalyzerHookType type) throws IOException {
        switch (type) {
            case VAULT -> {
                String basePackage = VaultHook.class.getPackage().getName();
                Skript.getAddonInstance().loadClasses(basePackage, "economy", "chat", "permission");
            }
            case REGIONS -> {
                String basePackage = RegionsPlugin.class.getPackage().getName();
                Skript.getAddonInstance().loadClasses(basePackage);
            }
        }
    }

    @Override
    public CompletableFuture<AnalyzeResults> loadScript(String path) {
        File file = new File(path);
        if (!file.exists() || (!file.isDirectory() && !file.getName().endsWith(".sk"))) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("provided file doesn't end with '.sk'"));
        }

        Set<File> scripts = ScriptUtils.listScripts(file);

        // unload already loaded scripts in this path
        ScriptLoader.unloadScripts(transformSet(
                scripts, ScriptLoader::getScript, Objects::nonNull
        ));

        try (CachingLogHandler logHandler = new CachingLogHandler().start()) {
            return ScriptLoader.loadScripts(scripts, logHandler).thenApply(info -> {
                Map<File, AnalyzeResult> structures = new HashMap<>();

                for (File scriptFile : scripts) {
                    Script script = ScriptLoader.getScript(scriptFile);
                    if (script == null) continue;

                    structures.put(scriptFile, new AnalyzeResult(
                            logHandler.getScriptErrors(scriptFile),
                            toScriptStructure(script)
                    ));
                }

                return new AnalyzeResults(structures);
            });
        }
    }

    @Override
    public boolean unloadScript(String path) {
        File file = new File(path);
        if (!file.exists() || (!file.isDirectory() && !file.getName().endsWith(".sk"))) {
            throw new IllegalArgumentException("provided file doesn't end with '.sk'");
        }

        Set<Script> scripts = file.isDirectory()
                ? transformSet(ScriptUtils.listScripts(file), ScriptLoader::getScript, Objects::nonNull)
                : transformValue(ScriptLoader.getScript(file), Collections::singleton, emptySet());
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
