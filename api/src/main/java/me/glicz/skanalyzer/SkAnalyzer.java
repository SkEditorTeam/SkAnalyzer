package me.glicz.skanalyzer;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import me.glicz.skanalyzer.result.AnalyzeResults;
import me.glicz.skanalyzer.server.AnalyzerServer;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Getter
public class SkAnalyzer {
    private final EnumSet<AnalyzerFlag> flags;
    private final Logger logger;
    private @MonotonicNonNull AnalyzerServer server;
    private boolean started;

    private SkAnalyzer(AnalyzerFlag[] flags) {
        this.flags = EnumSet.noneOf(AnalyzerFlag.class);
        this.flags.addAll(List.of(flags));

        this.logger = LoggerFactory.getLogger(getClass().getSimpleName());
    }

    @Contract(" -> new")
    public static Builder builder() {
        return new Builder();
    }

    public CompletableFuture<Void> start() {
        return start(false);
    }

    public CompletableFuture<Void> start(boolean daemon) {
        if (started) {
            return CompletableFuture.failedFuture(new IllegalStateException());
        }

        started = true;
        logger.info("Enabling...");

        return buildServer(daemon).thenAccept(server -> {
            this.server = server;
            logger.info("Successfully enabled. Have fun!");
        });
    }

    private CompletableFuture<AnalyzerServer> buildServer(boolean daemon) {
        CompletableFuture<AnalyzerServer> future = new CompletableFuture<>();

        Thread thread = new Thread(() -> {
            AnalyzerServer server = MockBukkit.mock(new AnalyzerServer(this));

            server.addSimpleWorld("world");

            server.getAddonsLoader().loadAddons();

            future.complete(server);

            server.startTicking();
        }, "Server Thread");
        thread.setDaemon(daemon);
        thread.start();

        return future;
    }

    @Unmodifiable
    public EnumSet<AnalyzerFlag> getFlags() {
        return EnumSet.copyOf(flags);
    }

    public CompletableFuture<AnalyzeResults> parseScript(String path) {
        return parseScript(path, false);
    }

    public CompletableFuture<AnalyzeResults> parseScript(String path, boolean load) {
        return server.getAddonsLoader().getMockSkriptBridge().parseScript(path, load);
    }

    public boolean unloadScript(String path) {
        return server.getAddonsLoader().getMockSkriptBridge().unloadScript(path);
    }

    public void unloadAllScripts() {
        server.getAddonsLoader().getMockSkriptBridge().unloadAllScripts();
    }

    @Data
    @Accessors(fluent = true, chain = true)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder {
        private AnalyzerFlag[] flags = {};

        public AnalyzerFlag[] flags() {
            return flags;
        }

        public Builder flags(AnalyzerFlag... flags) {
            this.flags = flags;
            return this;
        }

        public SkAnalyzer build() {
            return new SkAnalyzer(flags);
        }
    }
}
