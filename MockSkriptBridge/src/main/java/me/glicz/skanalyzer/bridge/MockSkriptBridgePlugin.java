package me.glicz.skanalyzer.bridge;

import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class MockSkriptBridgePlugin extends JavaPlugin {
    @Override
    public void onLoad() {
        getServer().getServicesManager().register(SkriptBridge.class, new MockSkriptBridgeImpl(), this, ServicePriority.Highest);
    }
}
