package me.glicz.skanalyzer.plugin.loader;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import me.glicz.skanalyzer.SkAnalyzer;
import me.glicz.skanalyzer.plugin.PluginClassLoader;
import me.glicz.skanalyzer.server.AnalyzerServer;
import org.apache.commons.io.FileUtils;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AnalyzerPluginLoader {
    public static final File PLUGINS_DIRECTORY = new File("plugins");

    private final Map<String, JavaPlugin> plugins = new LinkedHashMap<>();
    private final Logger logger = LoggerFactory.getLogger("PluginLoader");
    private final AnalyzerServer server;
    private final Set<File> extraPlugins;

    private @MonotonicNonNull ResolvedPluginLoadOrder pluginLoadOrder;

    public AnalyzerPluginLoader(AnalyzerServer server, Set<File> extraPlugins) {
        this.server = server;
        this.extraPlugins = extraPlugins;

        //noinspection ResultOfMethodCallIgnored
        PLUGINS_DIRECTORY.mkdirs();
    }

    private Collection<File> getPluginCandidates() {
        Collection<File> pluginCandidates = FileUtils.listFiles(
                PLUGINS_DIRECTORY, new String[]{"jar"}, false
        );

        pluginCandidates.addAll(extraPlugins);

        return pluginCandidates;
    }

    public void initPlugins() {
        Collection<File> candidates = getPluginCandidates();

        logger.info("Found plugin candidates: {}", String.join(", ", Iterables.transform(candidates, File::getName)));

        Map<String, ResolvedPlugin> resolvedPlugins = new HashMap<>();

        candidates.forEach(candidate -> {
            try {
                ResolvedPlugin plugin = resolvePlugin(candidate);
                String name = plugin.description().getName();

                Preconditions.checkState(
                        !plugins.containsKey(name),
                        "Plugin named '%s' already exists",
                        name
                );

                resolvedPlugins.put(name, plugin);
            } catch (Throwable e) {
                logger.atError()
                        .addArgument(candidate.getName())
                        .setCause(e)
                        .log("Something went wrong while trying to resolve plugin '{}'");
            }
        });

        pluginLoadOrder = new PluginLoadOrderResolver(resolvedPlugins).resolveLoadOrder();

        pluginLoadOrder.plugins().forEach(this::initPlugin);

        // not needed with GlobalClassLoaderGroup, however I want to look into it in the future
        /*
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
         */
    }

    private ResolvedPlugin resolvePlugin(File file) throws Exception {
        JarFile jarFile = new JarFile(file);
        PluginDescriptionFile description = loadPluginDescription(file, jarFile);

        PluginClassLoader classLoader = new PluginClassLoader(
                SkAnalyzer.class.getClassLoader(),
                description,
                new File(PLUGINS_DIRECTORY, description.getName()),
                file,
                jarFile
        );

        return new ResolvedPlugin(description, classLoader);
    }

    private PluginDescriptionFile loadPluginDescription(File file, JarFile jarFile) throws Exception {
        JarEntry entry = jarFile.getJarEntry("plugin.yml");
        if (entry != null) {
            try (InputStream in = jarFile.getInputStream(entry)) {
                return new PluginDescriptionFile(in);
            }
        }

        JarEntry paperEntry = jarFile.getJarEntry("paper-plugin.yml");
        if (paperEntry != null) {
            try (InputStream in = jarFile.getInputStream(paperEntry)) {
                return loadPaperPluginDescription(in);
            }
        }

        throw new IllegalArgumentException("Jar does not contain plugin.yml or paper-plugin.yml: " + file.getName());
    }

    private PluginDescriptionFile loadPaperPluginDescription(InputStream inputStream) throws InvalidDescriptionException {
        Yaml yaml = new Yaml();
        Object loaded = yaml.load(inputStream);
        if (!(loaded instanceof Map<?, ?> rawData)) {
            throw new IllegalArgumentException("Invalid paper-plugin.yml structure");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> data = new HashMap<>((Map<String, Object>) rawData);

        convertPaperDependencies(data);

        String dumpedYaml = yaml.dump(data);
        return new PluginDescriptionFile(new ByteArrayInputStream(dumpedYaml.getBytes(StandardCharsets.UTF_8)));
    }

    private void convertPaperDependencies(Map<String, Object> data) {
        Object depsObj = data.get("dependencies");
        if (!(depsObj instanceof Map<?, ?> depsMap)) {
            return;
        }

        List<String> depend = new ArrayList<>();
        List<String> softdepend = new ArrayList<>();
        List<String> loadBefore = new ArrayList<>();

        collectDependencies(depsMap.get("bootstrap"), depend, softdepend, loadBefore);
        collectDependencies(depsMap.get("server"), depend, softdepend, loadBefore);

        if (!depend.isEmpty()) data.put("depend", depend);
        if (!softdepend.isEmpty()) data.put("softdepend", softdepend);
        if (!loadBefore.isEmpty()) data.put("loadbefore", loadBefore);
    }

    private void collectDependencies(Object scopeObj, List<String> depend, List<String> softdepend, List<String> loadBefore) {
        if (!(scopeObj instanceof Map<?, ?> scopeMap)) {
            return;
        }

        for (Map.Entry<?, ?> entry : scopeMap.entrySet()) {
            String pluginName = String.valueOf(entry.getKey());
            boolean required = true;
            String loadOrder = "BEFORE";

            if (entry.getValue() instanceof Map<?, ?> conf) {
                if (conf.containsKey("required")) {
                    required = Boolean.parseBoolean(String.valueOf(conf.get("required")));
                }
                if (conf.containsKey("load")) {
                    loadOrder = String.valueOf(conf.get("load")).toUpperCase(Locale.ROOT);
                }
            }

            if ("AFTER".equals(loadOrder)) {
                loadBefore.add(pluginName);
            } else if (required) {
                depend.add(pluginName);
            } else {
                softdepend.add(pluginName);
            }
        }
    }

    private void initPlugin(ResolvedPlugin plugin) {
        String name = plugin.description().getName();

        try {
            //noinspection resource
            Class<?> mainClass = plugin.classLoader().loadClass(
                    plugin.description().getMainClass(),
                    true,
                    false,
                    false
            );

            plugins.put(name, (JavaPlugin) mainClass.getConstructor().newInstance());
        } catch (Throwable e) {
            logger.atError()
                    .addArgument(name)
                    .setCause(e)
                    .log("Something went wrong while trying to init plugin '{}'");
        }
    }

    public void loadPlugins() {
        plugins.values().forEach(plugin -> {
            try {
                plugin.getSLF4JLogger().info("Loading {}", plugin.getPluginMeta().getDisplayName());

                server.getPluginManager().registerLoadedPlugin(plugin);
            } catch (Throwable e) {
                logger.atError()
                        .addArgument(plugin.getName())
                        .setCause(e)
                        .log("Something went wrong while trying to load plugin '{}'");
            }
        });
    }

    public void enablePlugins(PluginLoadOrder loadOrder) {
        Collection<String> targetPlugins = pluginLoadOrder.pluginsByLoadOrder().get(loadOrder);

        plugins.values().forEach(plugin -> {
            if (!targetPlugins.contains(plugin.getName())) return;

            try {
                plugin.getSLF4JLogger().info("Enabling {}", plugin.getPluginMeta().getDisplayName());

                server.getPluginManager().enablePlugin(plugin);
            } catch (Throwable e) {
                logger.atError()
                        .addArgument(plugin.getName())
                        .setCause(e)
                        .log("Something went wrong while trying to enable plugin '{}'");
            }
        });
    }
}
