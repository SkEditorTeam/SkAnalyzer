package me.glicz.skanalyzer.bridge;

import me.glicz.skanalyzer.config.Config;
import me.glicz.skanalyzer.result.AnalyzeResults;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface SkriptBridge {
    void forceLoadHooks(Config.ForcedHooks forcedHooks) throws IOException;

    CompletableFuture<AnalyzeResults> loadScript(String path);

    boolean unloadScript(String path);

    void unloadAllScripts();
}
