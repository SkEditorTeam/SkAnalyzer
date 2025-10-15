package me.glicz.skanalyzer.config.provider.configurate;

import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.Callable;

public final class YamlConfigurateConfigProvider extends AbstractConfigurateConfigProvider {
    private static final int INDENT = 2;

    public YamlConfigurateConfigProvider(File file) {
        this(file.toPath());
    }

    public YamlConfigurateConfigProvider(Path path) {
        super(path);
    }

    public YamlConfigurateConfigProvider(
            @Nullable Callable<BufferedReader> source,
            @Nullable Callable<BufferedWriter> sink
    ) {
        super(source, sink);
    }

    @Override
    protected ConfigurationLoader<?> createLoader() {
        return YamlConfigurationLoader.builder().nodeStyle(NodeStyle.BLOCK).indent(INDENT).source(this.source).sink(this.sink).build();
    }
}
