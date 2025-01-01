package me.glicz.skanalyzer.loader;

import io.papermc.paper.plugin.provider.classloader.ConfiguredPluginClassLoader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.glicz.skanalyzer.SkAnalyzer;
import me.glicz.skanalyzer.bridge.MockSkriptBridge;
import me.glicz.skanalyzer.plugin.PluginClassLoader;
import me.glicz.skanalyzer.server.AnalyzerServer;
import org.apache.commons.io.FileUtils;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AddonsLoader {
    public static final String MOCK_SKRIPT_FILE = "MockSkript.jar";
    public static final String MOCK_SKRIPT_BRIDGE_FILE = "MockSkriptBridge.jar";

    private final Map<String, JavaPlugin> addons = new HashMap<>();
    private final SkAnalyzer skAnalyzer;
    private final AnalyzerServer server;
    private JavaPlugin skript;
    @Getter
    private MockSkriptBridge mockSkriptBridge;

    public File getAddonsDirectory() {
        return new File(skAnalyzer.getWorkingDirectory(), "Addons");
    }

    @SuppressWarnings({"deprecation"})
    public void loadAddons() {
        if (skript != null) {
            throw new RuntimeException("Addons are already loaded!");
        }

        skript = Objects.requireNonNull(initSimpleAddon(new File(getAddonsDirectory(), MOCK_SKRIPT_FILE)));
        loadAddon(skript);
        server.getPluginManager().enablePlugin(skript);

        mockSkriptBridge = Objects.requireNonNull(initMockSkriptBridge());
        loadAddon(mockSkriptBridge);
        server.getPluginManager().enablePlugin(mockSkriptBridge);

        FileUtils.listFiles(getAddonsDirectory(), new String[]{"jar"}, false).forEach(this::initSimpleAddon);

        List.copyOf(addons.values()).forEach(addon -> {
            try {
                loadAddon(addon);
            } catch (NullPointerException e) {
                skAnalyzer.getLogger().error("Something went wrong while trying to load %s".formatted(addon.getName()), e);
                addons.remove(addon.getName());
            }
        });

        addons.values().forEach(addon -> {
            try {
                server.getPluginManager().enablePlugin(addon);
            } catch (Exception e) {
                skAnalyzer.getLogger().error("Something went wrong while trying to enable %s".formatted(addon.getName()), e);
                server.getPluginManager().disablePlugin(addon);
            }
        });

        skAnalyzer.getLogger().info(
                "Successfully loaded addons: {}",
                addons.values().stream()
                        .filter(JavaPlugin::isEnabled)
                        .map(plugin -> plugin.getDescription().getFullName())
                        .collect(Collectors.joining(", "))
        );
    }

    private JavaPlugin initSimpleAddon(File file) {
        Class<?> pluginClass = initAddon(file);

        if (pluginClass == null) return null;

        try {
            JavaPlugin plugin = (JavaPlugin) pluginClass.getConstructor().newInstance();
            addons.putIfAbsent(plugin.getName(), plugin);
            return plugin;
        } catch (Exception e) {
            skAnalyzer.getLogger().error("Something went wrong while trying to init %s".formatted(file.getPath()), e);
        }

        return null;
    }

    private MockSkriptBridge initMockSkriptBridge() {
        File file = new File(getAddonsDirectory(), MOCK_SKRIPT_BRIDGE_FILE);
        Class<?> pluginClass = initAddon(file);

        if (pluginClass == null) return null;

        try {
            MockSkriptBridge plugin = (MockSkriptBridge) pluginClass.getConstructor(SkAnalyzer.class).newInstance(skAnalyzer);
            addons.putIfAbsent(plugin.getName(), plugin);
            return plugin;
        } catch (Exception e) {
            skAnalyzer.getLogger().error("Something went wrong while trying to init %s".formatted(file.getPath()), e);
        }

        return null;
    }

    @SuppressWarnings("UnstableApiUsage")
    private Class<?> initAddon(File file) {
        if (skript != null && file.getName().equals(MOCK_SKRIPT_FILE)) return null;
        if (mockSkriptBridge != null && file.getName().equals(MOCK_SKRIPT_BRIDGE_FILE)) return null;

        try {
            JarFile jarFile = new JarFile(file);
            PluginDescriptionFile description = new PluginDescriptionFile(jarFile.getInputStream(jarFile.getEntry("plugin.yml")));

            if (addons.containsKey(description.getName())) {
                throw new RuntimeException("Plugin named '%s' is already loaded".formatted(description.getName()));
            }

            PluginClassLoader classLoader = new PluginClassLoader(
                    SkAnalyzer.class.getClassLoader(),
                    description,
                    new File(getAddonsDirectory(), description.getName()),
                    file,
                    jarFile
            );

            if (skript != null) {
                classLoader.getGroup().add((ConfiguredPluginClassLoader) skript.getClass().getClassLoader());
            }

            return classLoader.loadClass(description.getMainClass(), true, false, false);
        } catch (Exception | ExceptionInInitializerError e) {
            skAnalyzer.getLogger().error("Something went wrong while trying to init %s".formatted(file.getPath()), e);
        }

        return null;
    }

    @SuppressWarnings({"deprecation", "UnstableApiUsage"})
    private void loadAddon(JavaPlugin addon) {
        if (server.getPluginManager().getPlugin(addon.getName()) != null) return;

        PluginClassLoader classLoader = (PluginClassLoader) addon.getClass().getClassLoader();

        addon.getDescription().getDepend().forEach(depend -> {
            if (!addons.containsKey(depend)) {
                throw new NullPointerException("Missing dependency: " + depend);
            }

            if (depend.equals("Skript")) {
                return;
            }

            classLoader.getGroup().add((ConfiguredPluginClassLoader) addons.get(depend).getClass().getClassLoader());
        });

        addon.getDescription().getSoftDepend().forEach(softDepend -> {
            if (!addons.containsKey(softDepend) || softDepend.equals("Skript")) {
                return;
            }

            classLoader.getGroup().add((ConfiguredPluginClassLoader) addons.get(softDepend).getClass().getClassLoader());
        });

        server.getPluginManager().registerLoadedPlugin(addon);
    }
}
