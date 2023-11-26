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
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    public static final String WORKING_DIR_ENV = "skanalyzer.workingDir";
    private final EnumSet<AnalyzerFlag> flags;
    private final File workingDirectory;
    private final Logger logger;
    private final AnalyzerServer server;
    private final AddonsLoader addonsLoader;

    private SkAnalyzer(AnalyzerFlag[] flags, File workingDirectory) {
        this.flags = EnumSet.copyOf(List.of(flags));
        this.workingDirectory = Objects.requireNonNullElse(workingDirectory, AddonsLoader.ADDONS);
        this.logger = LogManager.getLogger(getFlags().contains(AnalyzerFlag.ENABLE_PLAIN_LOGGER) ? "PlainLogger" : "SkAnalyzer");

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

    public CompletableFuture<ScriptAnalyzeResult> parseScript(String path) {
        return addonsLoader.getMockSkriptBridge().parseScript(path);
    }

    private void extractEmbeddedAddons() {
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
        private File workingDirectory = Optional.ofNullable(System.getenv(WORKING_DIR_ENV))
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
            return new SkAnalyzer(flags, workingDirectory);
        }
    }
}
