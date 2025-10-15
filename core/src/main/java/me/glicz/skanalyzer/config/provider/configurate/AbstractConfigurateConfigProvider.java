package me.glicz.skanalyzer.config.provider.configurate;

import me.glicz.skanalyzer.config.Config;
import me.glicz.skanalyzer.config.provider.ConfigProvider;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.AtomicFiles;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import static java.util.Objects.requireNonNullElse;

public abstract class AbstractConfigurateConfigProvider implements ConfigProvider {
    private static final Callable<BufferedReader> NULL_SOURCE = () -> new BufferedReader(Reader.nullReader());
    private static final Callable<BufferedWriter> NULL_SINK = () -> new BufferedWriter(Writer.nullWriter());

    protected final Callable<BufferedReader> source;
    protected final Callable<BufferedWriter> sink;

    protected AbstractConfigurateConfigProvider(Path path) {
        Path absolutePath = path.toAbsolutePath();

        this.source = () -> Files.newBufferedReader(absolutePath, StandardCharsets.UTF_8);
        this.sink = AtomicFiles.atomicWriterFactory(absolutePath, StandardCharsets.UTF_8);
    }

    protected AbstractConfigurateConfigProvider(
            @Nullable Callable<BufferedReader> source,
            @Nullable Callable<BufferedWriter> sink
    ) {
        this.source = requireNonNullElse(source, NULL_SOURCE);
        this.sink = requireNonNullElse(sink, NULL_SINK);
    }

    protected abstract ConfigurationLoader<?> createLoader();

    @Override
    public Config loadConfig() throws IOException {
        ConfigurationLoader<?> loader = createLoader();

        ConfigurationNode node = loader.load();
        Config config = node.<Config>get(Config.class, Config::new);

        loader.save(loader.createNode().set(config));

        return config;
    }
}
