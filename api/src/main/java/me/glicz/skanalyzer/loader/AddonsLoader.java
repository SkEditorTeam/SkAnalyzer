package me.glicz.skanalyzer.loader;

import io.papermc.paper.plugin.provider.classloader.ConfiguredPluginClassLoader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
public class AddonsLoader {
    public static final File USER_HOME = new File(System.getProperty("user.home"));
    public static final File ADDONS = new File(USER_HOME, "SkAnalyzer/addons");
    public static final String MOCK_SKRIPT = "MockSkript.jar";
    public static final String MOCK_SKRIPT_BRIDGE = "MockSkriptBridge.jar";
    private final SkAnalyzer skAnalyzer;
    private JavaPlugin skript;
    @Getter
    private MockSkriptBridge mockSkriptBridge;

    @SuppressWarnings("UnstableApiUsage")
    public void loadAddons() {
        if (skript != null)
            throw new RuntimeException("Addons are already loaded!");
        skript = loadSimpleAddon(new File(skAnalyzer.getWorkingDirectory(), MOCK_SKRIPT));
        mockSkriptBridge = loadMockSkriptBridge();
        FileUtils.listFiles(skAnalyzer.getWorkingDirectory(), new String[]{"jar"}, false).forEach(this::loadSimpleAddon);
        skAnalyzer.getLogger().info(
                "Successfully loaded addons: {}",
                Arrays.stream(Bukkit.getPluginManager().getPlugins())
                        .map(plugin -> plugin.getPluginMeta().getDisplayName())
                        .collect(Collectors.joining(", "))
        );
    }

    private JavaPlugin loadSimpleAddon(File file) {
        Class<?> pluginClass = loadAddon(file);
        if (pluginClass == null) return null;
        try {
            JavaPlugin plugin = (JavaPlugin) pluginClass.getConstructor().newInstance();
            skAnalyzer.getServer().getPluginManager().registerLoadedPlugin(plugin);
            skAnalyzer.getServer().getPluginManager().enablePlugin(plugin);
            return plugin;
        } catch (Exception e) {
            skAnalyzer.getLogger().error("Something went wrong while trying to load %s".formatted(file.getPath()), e);
        }
        return null;
    }

    private MockSkriptBridge loadMockSkriptBridge() {
        File file = new File(skAnalyzer.getWorkingDirectory(), MOCK_SKRIPT_BRIDGE);
        Class<?> pluginClass = loadAddon(file);
        if (pluginClass == null) return null;
        try {
            MockSkriptBridge plugin = (MockSkriptBridge) pluginClass.getConstructor(SkAnalyzer.class).newInstance(skAnalyzer);
            skAnalyzer.getServer().getPluginManager().registerLoadedPlugin(plugin);
            skAnalyzer.getServer().getPluginManager().enablePlugin(plugin);
            return plugin;
        } catch (Exception e) {
            skAnalyzer.getLogger().error("Something went wrong while trying to load %s".formatted(file.getPath()), e);
        }
        return null;
    }

    @SuppressWarnings("UnstableApiUsage")
    private Class<?> loadAddon(File file) {
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
                    new File(skAnalyzer.getWorkingDirectory(), description.getName()),
                    file,
                    jarFile
            );
            if (skript != null)
                classLoader.getGroup().add((ConfiguredPluginClassLoader) skript.getClass().getClassLoader());
            return classLoader.loadClass(description.getMainClass(), true, false, false);
        } catch (Exception | ExceptionInInitializerError e) {
            skAnalyzer.getLogger().error("Something went wrong while trying to load %s".formatted(file.getPath()), e);
        }
        return null;
    }
}
