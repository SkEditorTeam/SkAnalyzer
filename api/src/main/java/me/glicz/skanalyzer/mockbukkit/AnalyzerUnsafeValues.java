package me.glicz.skanalyzer.mockbukkit;

import be.seeseemelk.mockbukkit.MockUnsafeValues;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.BooleanSupplier;

@SuppressWarnings({"deprecation", "UnstableApiUsage"})
public class AnalyzerUnsafeValues extends MockUnsafeValues {
    @Override
    public ItemStack modifyItemStack(ItemStack stack, String arguments) {
        return stack;
    }

    @Override
    public LifecycleEventManager<Plugin> createPluginLifecycleEventManager(JavaPlugin javaPlugin, BooleanSupplier booleanSupplier) {
        return null;
    }
}
