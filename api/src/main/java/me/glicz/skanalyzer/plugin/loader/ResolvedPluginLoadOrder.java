package me.glicz.skanalyzer.plugin.loader;

import com.google.common.collect.Multimap;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

record ResolvedPluginLoadOrder(
        List<JavaPlugin> plugins,
        Multimap<PluginLoadOrder, String> pluginsByLoadOrder
) {
}
