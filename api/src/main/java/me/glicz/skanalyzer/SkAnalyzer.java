package me.glicz.skanalyzer;

import be.seeseemelk.mockbukkit.MockBukkit;
import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import me.glicz.skanalyzer.loader.AddonsLoader;
import me.glicz.skanalyzer.mockbukkit.AnalyzerServer;
import me.glicz.skanalyzer.result.ScriptAnalyzeResults;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Getter
public class SkAnalyzer {
    public static final String LOGGER_TYPE_PROPERTY = "skanalyzer.loggerType";
    public static final String WORKING_DIR_PROPERTY = "skanalyzer.workingDir";
    private final EnumSet<AnalyzerFlag> flags;
    private final LoggerType loggerType;
    private final File workingDirectory;
    private final Logger logger;
    private final AnalyzerServer server;
    private final AddonsLoader addonsLoader;

    private SkAnalyzer(AnalyzerFlag[] flags, LoggerType loggerType, File workingDirectory) {
        this.flags = EnumSet.noneOf(AnalyzerFlag.class);
        this.flags.addAll(List.of(flags));
        this.loggerType = loggerType;
        this.workingDirectory = Objects.requireNonNullElse(workingDirectory, AddonsLoader.ADDONS);
        this.logger = LogManager.getLogger(loggerType.getLoggerName());
        Configurator.setLevel(logger, loggerType.getLoggerLevel());

        logger.info("Enabling...");

        this.server = MockBukkit.mock(new AnalyzerServer());

        extractEmbeddedAddons();

        this.addonsLoader = new AddonsLoader(this);
        this.addonsLoader.loadAddons();

        server.startTicking();
        logger.info("Successfully enabled. Have fun!");
    }

    @Contract(" -> new")
    public static @NotNull Builder builder() {
        return new Builder();
    }

    @Unmodifiable
    public EnumSet<AnalyzerFlag> getFlags() {
        return EnumSet.copyOf(flags);
    }

    public CompletableFuture<ScriptAnalyzeResults> parseScript(String path) {
        return parseScript(path, false);
    }

    public CompletableFuture<ScriptAnalyzeResults> parseScript(String path, boolean load) {
        return addonsLoader.getMockSkriptBridge().parseScript(path, load);
    }

    public boolean unloadScript(String path) {
        return addonsLoader.getMockSkriptBridge().unloadScript(path);
    }

    public void unloadAllScripts() {
        addonsLoader.getMockSkriptBridge().unloadAllScripts();
    }

    private void extractEmbeddedAddons() {
        if (flags.contains(AnalyzerFlag.SKIP_EXTRACTING_ADDONS)) {
            logger.warn("{} flag is present! This means that default embedded addons (and Skript) won't be extracted. " +
                            "If you're not sure what may it cause, remove it immediately!",
                    AnalyzerFlag.SKIP_EXTRACTING_ADDONS.name()
            );
            return;
        }

        logger.info("Extracting embedded addons...");

        extractEmbeddedAddon(AddonsLoader.MOCK_SKRIPT);
        extractEmbeddedAddon(AddonsLoader.MOCK_SKRIPT_BRIDGE);

        logger.info("Successfully extracted embedded addons!");
    }

    private void extractEmbeddedAddon(String name) {
        try (InputStream embeddedJar = getClass().getClassLoader().getResourceAsStream(name + ".embedded")) {
            Preconditions.checkArgument(embeddedJar != null, "Couldn't find embedded %s", name);
            FileUtils.copyInputStreamToFile(embeddedJar, new File(workingDirectory, name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Data
    @Accessors(fluent = true, chain = true)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder {
        private AnalyzerFlag[] flags = {};
        private LoggerType loggerType = Optional.ofNullable(System.getProperty(LOGGER_TYPE_PROPERTY))
                .map(loggerType -> EnumUtils.getEnumIgnoreCase(LoggerType.class, loggerType))
                .orElse(LoggerType.NORMAL);
        private File workingDirectory = Optional.ofNullable(System.getProperty(WORKING_DIR_PROPERTY))
                .map(File::new)
                .orElse(null);

        public AnalyzerFlag[] flags() {
            return flags;
        }

        public Builder flags(@NotNull AnalyzerFlag @NotNull ... flags) {
            this.flags = flags;
            return this;
        }

        public SkAnalyzer build() {
            return new SkAnalyzer(flags, loggerType, workingDirectory);
        }
    }
}
