package me.glicz.skanalyzer.loader;

import io.papermc.paper.plugin.provider.classloader.ConfiguredPluginClassLoader;
import lombok.Getter;
import me.glicz.skanalyzer.SkAnalyzer;
import me.glicz.skanalyzer.bridge.MockSkriptBridge;
import me.glicz.skanalyzer.mockbukkit.AnalyzerClassLoader;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class AddonsLoader {
    public static final File USER_HOME = new File(System.getProperty("user.home"));
    public static final File ADDONS = new File(USER_HOME, "SkAnalyzer/addons");
    public static final String MOCK_SKRIPT = "MockSkript.jar";
    public static final String MOCK_SKRIPT_BRIDGE = "MockSkriptBridge.jar";
    private static JavaPlugin skript;
    @Getter
    private static MockSkriptBridge mockSkriptBridge;

    @SuppressWarnings("UnstableApiUsage")
    public static void loadAddons() {
        if (skript != null)
            throw new RuntimeException("Addons are already loaded!");
        skript = loadAddon(new File(ADDONS, MOCK_SKRIPT));
        mockSkriptBridge = (MockSkriptBridge) loadAddon(new File(ADDONS, MOCK_SKRIPT_BRIDGE));
        FileUtils.listFiles(ADDONS, new String[]{"jar"}, false).forEach(AddonsLoader::loadAddon);
        SkAnalyzer.get().getLogger().info(
                "Successfully loaded addons: {}",
                Arrays.stream(Bukkit.getPluginManager().getPlugins())
                        .map(plugin -> plugin.getPluginMeta().getDisplayName())
                        .collect(Collectors.joining(", "))
        );
    }

    @SuppressWarnings("UnstableApiUsage")
    private static JavaPlugin loadAddon(File file) {
        if (skript != null && file.getName().equals(MOCK_SKRIPT)) return null;
        if (mockSkriptBridge != null && file.getName().equals(MOCK_SKRIPT_BRIDGE)) return null;
        try {
            JarFile jarFile = new JarFile(file);
            PluginDescriptionFile description = new PluginDescriptionFile(jarFile.getInputStream(jarFile.getEntry("plugin.yml")));
            if (Bukkit.getServer().getPluginManager().isPluginEnabled(description.getName()))
                throw new RuntimeException("Plugin named '%s' is already loaded".formatted(description.getName()));
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
