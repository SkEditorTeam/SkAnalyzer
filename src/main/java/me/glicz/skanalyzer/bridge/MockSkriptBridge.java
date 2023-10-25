package me.glicz.skanalyzer.bridge;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public abstract class MockSkriptBridge extends JavaPlugin {
    public abstract void parseArgs(List<String> args);

    public abstract void parseScript(String path);
}
