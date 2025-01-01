package me.glicz.skanalyzer.bridge;

import lombok.AllArgsConstructor;
import me.glicz.skanalyzer.SkAnalyzer;
import me.glicz.skanalyzer.result.AnalyzeResults;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
public abstract class MockSkriptBridge extends JavaPlugin {
    protected final SkAnalyzer skAnalyzer;

    public abstract CompletableFuture<AnalyzeResults> parseScript(String path, boolean load);

    public abstract boolean unloadScript(String path);

    public abstract void unloadAllScripts();
}
