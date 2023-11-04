package me.glicz.skanalyzer.loader;

import io.papermc.paper.plugin.provider.classloader.ConfiguredPluginClassLoader;
import lombok.Getter;
import me.glicz.skanalyzer.SkAnalyzer;
import me.glicz.skanalyzer.bridge.MockSkriptBridge;
import me.glicz.skanalyzer.mockbukkit.AnalyzerClassLoader;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.jar.JarFile;

public class AddonsLoader {
    public static final File USER_HOME = new File(System.getProperty("user.home"));
    public static final File ADDONS = new File(USER_HOME, "SkAnalyzer/addons");
    private static JavaPlugin skript;
    @Getter
    private static MockSkriptBridge mockSkriptBridge;

    public static void loadAddons() {
        if (skript != null)
            throw new RuntimeException("Addons are already loaded!");
        skript = loadAddon(new File(ADDONS, "MockSkript.jar")); // currently only Skript support :(
        mockSkriptBridge = (MockSkriptBridge) loadAddon(new File(ADDONS, "MockSkriptBridge.jar"));
    }

    @SuppressWarnings("UnstableApiUsage")
    private static JavaPlugin loadAddon(File file) {
        try {
            JarFile jarFile = new JarFile(file);
            PluginDescriptionFile description = new PluginDescriptionFile(jarFile.getInputStream(jarFile.getEntry("plugin.yml")));
            AnalyzerClassLoader classLoader = new AnalyzerClassLoader(
                    SkAnalyzer.class.getClassLoader(),
                    description,
                    new File(AddonsLoader.ADDONS, description.getName()),
                    file,
                    jarFile
            );
            if (skript != null)
                classLoader.getGroup().add((ConfiguredPluginClassLoader) skript.getClass().getClassLoader());
            Class<?> pluginClass = classLoader.loadClass(description.getMainClass(), true, false, false);
            JavaPlugin plugin = (JavaPlugin) pluginClass.getConstructor().newInstance();
            SkAnalyzer.get().getServer().getPluginManager().registerLoadedPlugin(plugin);
            SkAnalyzer.get().getServer().getPluginManager().enablePlugin(plugin);
            return plugin;
        } catch (Exception e) {
            SkAnalyzer.get().getLogger().error("Something went wrong while trying to load %s".formatted(file.getPath()), e);
        }
        return null;
    }

}
