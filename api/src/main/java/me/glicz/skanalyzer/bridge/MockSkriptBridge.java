package me.glicz.skanalyzer.bridge;

import lombok.AllArgsConstructor;
import me.glicz.skanalyzer.ScriptAnalyzeResult;
import me.glicz.skanalyzer.SkAnalyzer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
public abstract class MockSkriptBridge extends JavaPlugin {
    protected final SkAnalyzer skAnalyzer;

    public abstract CompletableFuture<ScriptAnalyzeResult> parseScript(String path);
}
