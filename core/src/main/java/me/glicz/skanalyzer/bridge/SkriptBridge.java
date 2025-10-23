package me.glicz.skanalyzer.bridge;

import me.glicz.skanalyzer.config.Config;
import me.glicz.skanalyzer.result.AnalyzeResults;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface SkriptBridge {
    void forceLoadHooks(Config.ForcedHooks forcedHooks) throws IOException;

    default CompletableFuture<AnalyzeResults> loadScripts(File... files) {
        return loadScripts(Set.of(files), true);
    }

    CompletableFuture<AnalyzeResults> loadScripts(Set<File> files, boolean validateFiles);

    boolean unloadScripts(File... files) throws IOException;

    void unloadAllScripts();
}
