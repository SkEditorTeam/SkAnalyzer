package me.glicz.skanalyzer.bridge;

import me.glicz.skanalyzer.config.Config;
import me.glicz.skanalyzer.result.AnalyzeResults;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public abstract class MockSkriptBridge {
    public abstract void forceLoadHooks(Config.ForcedHooks forcedHooks) throws IOException;

    public abstract CompletableFuture<AnalyzeResults> loadScript(String path);

    public abstract boolean unloadScript(String path);

    public abstract void unloadAllScripts();
}
