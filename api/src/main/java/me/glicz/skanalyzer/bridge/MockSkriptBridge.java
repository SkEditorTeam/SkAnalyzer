package me.glicz.skanalyzer.bridge;

import me.glicz.skanalyzer.AnalyzerHookType;
import me.glicz.skanalyzer.result.AnalyzeResults;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public abstract class MockSkriptBridge {
    public abstract void forceLoadHook(AnalyzerHookType type) throws IOException;

    public abstract CompletableFuture<AnalyzeResults> parseScript(String path, boolean load);

    public abstract boolean unloadScript(String path);

    public abstract void unloadAllScripts();
}
