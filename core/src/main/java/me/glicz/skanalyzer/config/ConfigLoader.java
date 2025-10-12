package me.glicz.skanalyzer.config;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;

public final class ConfigLoader {
    public static final File CONFIG_FILE = new File("config.yml");

    private ConfigLoader() {
    }

    public static Config loadConfig() throws ConfigurateException {
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK)
                .indent(2)
                .file(CONFIG_FILE)
                .build();

        CommentedConfigurationNode node = loader.load();
        Config config = node.get(Config.class);

        loader.save(loader.createNode().set(config));

        return config;
    }
}
