package me.glicz.skanalyzer.plugin.loader;

import me.glicz.skanalyzer.plugin.PluginClassLoader;
import org.bukkit.plugin.PluginDescriptionFile;

record ResolvedPlugin(PluginDescriptionFile description, PluginClassLoader classLoader) {
}
