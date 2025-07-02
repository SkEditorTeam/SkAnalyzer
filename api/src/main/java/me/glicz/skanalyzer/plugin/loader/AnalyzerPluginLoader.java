package me.glicz.skanalyzer.plugin.loader;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import io.papermc.paper.plugin.provider.classloader.ConfiguredPluginClassLoader;
import me.glicz.skanalyzer.SkAnalyzer;
import me.glicz.skanalyzer.plugin.PluginClassLoader;
import me.glicz.skanalyzer.server.AnalyzerServer;
import org.apache.commons.io.FileUtils;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

public class AnalyzerPluginLoader {
    public static final File PLUGINS_DIRECTORY = new File("plugins");

    private final Map<String, JavaPlugin> plugins = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger("PluginLoader");
    private final AnalyzerServer server;
    private @MonotonicNonNull ResolvedPluginLoadOrder pluginLoadOrder;

    public AnalyzerPluginLoader(AnalyzerServer server) {
        this.server = server;

        //noinspection ResultOfMethodCallIgnored
        PLUGINS_DIRECTORY.mkdirs();
    }

    private Collection<File> getPluginCandidates() {
        return FileUtils.listFiles(PLUGINS_DIRECTORY, new String[]{"jar"}, false);
    }

    @SuppressWarnings("UnstableApiUsage")
    public void initPlugins() {
        Collection<File> candidates = getPluginCandidates();

        logger.info("Found plugin candidates: {}", String.join(", ", Iterables.transform(candidates, File::getName)));

        candidates.forEach(candidate -> {
            JavaPlugin plugin = initPlugin(candidate);
            if (plugin == null) return;

            plugins.put(plugin.getName(), plugin);
        });

        pluginLoadOrder = new PluginLoadOrderResolver(plugins).resolveLoadOrder();

        pluginLoadOrder.plugins().forEach(plugin -> {
            PluginClassLoader classLoader = (PluginClassLoader) plugin.getClass().getClassLoader();

            plugin.getPluginMeta().getPluginDependencies().forEach(depend ->
                    classLoader.getGroup().add((ConfiguredPluginClassLoader) plugins.get(depend).getClass().getClassLoader())
            );

            plugin.getPluginMeta().getPluginSoftDependencies().forEach(softDepend -> {
                if (!plugins.containsKey(softDepend)) {
                    return;
                }

                classLoader.getGroup().add((ConfiguredPluginClassLoader) plugins.get(softDepend).getClass().getClassLoader());
            });

        });
    }

    private @Nullable JavaPlugin initPlugin(File file) {
        try {
            JarFile jarFile = new JarFile(file);
            PluginDescriptionFile description = new PluginDescriptionFile(jarFile.getInputStream(jarFile.getEntry("plugin.yml")));

            Preconditions.checkState(
                    !plugins.containsKey(description.getName()),
                    "Plugin named '%s' already exists",
                    description.getName()
            );

            PluginClassLoader classLoader = new PluginClassLoader(
                    SkAnalyzer.class.getClassLoader(),
                    description,
                    new File(PLUGINS_DIRECTORY, description.getName()),
                    file,
                    jarFile
            );

            Class<?> mainClass = classLoader.loadClass(
                    description.getMainClass(),
                    true,
                    false,
                    false
            );

            return (JavaPlugin) mainClass.getConstructor().newInstance();
        } catch (Throwable e) {
            logger.atError()
                    .addArgument(file.getName())
                    .setCause(e)
                    .log("Something went wrong while trying to init {}");
        }

        return null;
    }

    public void loadPlugins() {
        pluginLoadOrder.plugins().forEach(plugin -> {
            try {
                //noinspection UnstableApiUsage
                plugin.getSLF4JLogger().info("Loading {}", plugin.getPluginMeta().getDisplayName());

                server.getPluginManager().registerLoadedPlugin(plugin);
            } catch (Throwable e) {
                logger.atError()
                        .addArgument(plugin.getName())
                        .setCause(e)
                        .log("Something went wrong while trying to load {}");
            }
        });
    }

    public void enablePlugins(PluginLoadOrder loadOrder) {
        Collection<String> plugins = pluginLoadOrder.pluginsByLoadOrder().get(loadOrder);

        pluginLoadOrder.plugins().forEach(plugin -> {
            if (!plugins.contains(plugin.getName())) return;

            try {
                //noinspection UnstableApiUsage
                plugin.getSLF4JLogger().info("Enabling {}", plugin.getPluginMeta().getDisplayName());

                server.getPluginManager().enablePlugin(plugin);
            } catch (Throwable e) {
                logger.atError()
                        .addArgument(plugin.getName())
                        .setCause(e)
                        .log("Something went wrong while trying to enable {}");
            }
        });
    }
}
