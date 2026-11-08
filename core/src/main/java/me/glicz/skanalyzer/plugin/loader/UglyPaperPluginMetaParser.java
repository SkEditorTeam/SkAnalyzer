package me.glicz.skanalyzer.plugin.loader;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoadOrder;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

// temporary workaround for paper plugins...
final class UglyPaperPluginMetaParser {
    private UglyPaperPluginMetaParser() {
    }

    public static PluginDescriptionFile parse(InputStream is) throws IOException {
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .indent(2)
                .nodeStyle(NodeStyle.BLOCK)
                .headerMode(HeaderMode.NONE)
                .source(() -> new BufferedReader(new InputStreamReader(is)))
                .build();

        CommentedConfigurationNode node = loader.load();

        String name = node.node("name").require(String.class);

        ArrayList<String> authors = new ArrayList<>(0);

        node.node("authors").act(authorsNode -> {
            if (!authorsNode.virtual()) {
                authors.addAll(authorsNode.getList(String.class, List.of()));
            }
        });

        node.node("author").act(authorNode -> {
            if (!authorNode.virtual()) {
                authors.add(authorNode.require(String.class));
            }
        });

        List<String> dependencies = new ArrayList<>(0);
        List<String> softDependencies = new ArrayList<>(0);

        collectDependencies(node.node("dependencies", "bootstrap"), dependencies, softDependencies);
        collectDependencies(node.node("dependencies", "server"), dependencies, softDependencies);

        ConfigurationNode permissionsNode = node.node("permissions");
        ConfigurationNode permissionDefaultNode = permissionsNode.node("defaultPerm");

        PermissionDefault permissionDefault;
        ArrayList<Permission> permissions = new ArrayList<>(0);

        if (!permissionDefaultNode.virtual()) {
            permissionDefault = PermissionDefault.getByName(permissionDefaultNode.require(String.class));
        } else {
            permissionDefault = PermissionDefault.OP;
        }

        if (!permissionsNode.virtual()) {
            Map<Object, ? extends ConfigurationNode> childrenMap = permissionsNode.childrenMap();

            permissions.ensureCapacity(childrenMap.size());

            childrenMap.forEach((name0, data0) -> {
                Map<?, ?> data = Objects.requireNonNull((Map<?, ?>) data0.raw(), "data");

                Permission.loadPermission(String.valueOf(name0), data, permissionDefault, permissions);
            });
        }

        @SuppressWarnings("UnstableApiUsage")
        PluginDescriptionFile description = new PluginDescriptionFile(
                name,
                name.replace(' ', '_'),
                List.of(),
                node.node("main").require(String.class),
                null,
                List.copyOf(dependencies),
                List.copyOf(softDependencies),
                List.of(),
                node.node("version").require(String.class),
                Map.of(),
                node.node("description").get(String.class),
                List.copyOf(authors),
                node.node("contributors").getList(String.class, List.of()),
                node.node("website").get(String.class),
                node.node("prefix").get(String.class),
                node.node("load").get(PluginLoadOrder.class, PluginLoadOrder.POSTWORLD),
                List.copyOf(permissions),
                permissionDefault,
                Set.of(),
                node.node("api-version").require(String.class),
                List.of()
        );
        return description;
    }

    private static void collectDependencies(
            ConfigurationNode dependenciesNode, List<String> dependencies, List<String> softDependencies
    ) {
        dependenciesNode.childrenMap().forEach((name0, dependencyNode) -> {
            String name = String.valueOf(name0);
            boolean required = dependencyNode.node("required").getBoolean();

            if (required) {
                dependencies.add(name);
            } else {
                softDependencies.add(name);
            }
        });
    }
}
