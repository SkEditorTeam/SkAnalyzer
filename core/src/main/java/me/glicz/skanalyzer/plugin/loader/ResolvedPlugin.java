package me.glicz.skanalyzer.plugin.loader;

import io.papermc.paper.plugin.configuration.PluginMeta;
import me.glicz.skanalyzer.plugin.PluginClassLoader;

record ResolvedPlugin(PluginMeta meta, PluginClassLoader classLoader) {
}
