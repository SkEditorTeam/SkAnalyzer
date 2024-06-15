package me.glicz.skanalyzer.bridge;

import lombok.AllArgsConstructor;
import me.glicz.skanalyzer.SkAnalyzer;
import me.glicz.skanalyzer.result.ScriptAnalyzeResults;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
public abstract class MockSkriptBridge extends JavaPlugin {
    protected final SkAnalyzer skAnalyzer;

    public abstract CompletableFuture<ScriptAnalyzeResults> parseScript(String path, boolean load);

    public abstract void testScripts(String path);

    public abstract boolean unloadScript(String path);

    public abstract void unloadAllScripts();
}
