package me.glicz.skanalyzer.plugin.loader;

import com.google.common.collect.Multimap;
import org.bukkit.plugin.PluginLoadOrder;

import java.util.List;

record ResolvedPluginLoadOrder(
        List<ResolvedPlugin> plugins,
        Multimap<PluginLoadOrder, String> pluginsByLoadOrder
) {
}
