package me.glicz.skanalyzer.plugin.loader;

import com.google.common.collect.Multimaps;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.java.JavaPlugin;
import org.jgrapht.Graph;
import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.*;

class PluginLoadOrderResolver {
    private final Map<String, JavaPlugin> plugins;
    private final Graph<String, DefaultEdge> dependencyGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
    private final Map<String, PluginLoadOrder> loadOrderMap = new HashMap<>();

    public PluginLoadOrderResolver(Map<String, JavaPlugin> plugins) {
        this.plugins = plugins;
        buildDependencyGraph();
    }

    @SuppressWarnings("UnstableApiUsage")
    private void buildDependencyGraph() {
        for (JavaPlugin plugin : plugins.values()) {
            dependencyGraph.addVertex(plugin.getName());
            loadOrderMap.put(plugin.getName(), plugin.getPluginMeta().getLoadOrder());
        }

        for (JavaPlugin plugin : plugins.values()) {
            String name = plugin.getName();

            for (String depend : plugin.getPluginMeta().getPluginDependencies()) {
                dependencyGraph.addEdge(depend, name);
                adjustLoadOrder(name, depend);
            }

            for (String softDepend : plugin.getPluginMeta().getPluginSoftDependencies()) {
                if (dependencyGraph.containsVertex(softDepend)) {
                    dependencyGraph.addEdge(softDepend, name);
                    adjustLoadOrder(name, softDepend);
                }
            }

            for (String loadBefore : plugin.getPluginMeta().getLoadBeforePlugins()) {
                if (dependencyGraph.containsVertex(loadBefore)) {
                    dependencyGraph.addEdge(loadBefore, name);
                    adjustLoadOrder(name, loadBefore);
                }
            }
        }

        CycleDetector<String, DefaultEdge> cycleDetector = new CycleDetector<>(dependencyGraph);
        if (cycleDetector.detectCycles()) {
            throw new IllegalStateException("Circular dependency detected in plugin load order!");
        }
    }

    private void adjustLoadOrder(String dependent, String dependency) {
        if (loadOrderMap.get(dependent) == PluginLoadOrder.STARTUP && loadOrderMap.get(dependency) == PluginLoadOrder.POSTWORLD) {
            loadOrderMap.put(dependent, PluginLoadOrder.POSTWORLD);
        }
    }

    public ResolvedPluginLoadOrder resolveLoadOrder() {
        List<JavaPlugin> sortedPlugins = new ArrayList<>();
        TopologicalOrderIterator<String, DefaultEdge> iterator = new TopologicalOrderIterator<>(dependencyGraph);

        while (iterator.hasNext()) {
            JavaPlugin plugin = plugins.get(iterator.next());

            if (plugin != null) {
                sortedPlugins.add(plugin);
            }
        }

        sortedPlugins.sort(Comparator.comparing(plugin -> loadOrderMap.get(plugin.getName())));

        return new ResolvedPluginLoadOrder(
                List.copyOf(sortedPlugins),
                Multimaps.index(loadOrderMap.keySet(), loadOrderMap::get)
        );
    }
}

