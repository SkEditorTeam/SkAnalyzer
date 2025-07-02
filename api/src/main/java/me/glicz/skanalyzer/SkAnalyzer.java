package me.glicz.skanalyzer;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import me.glicz.skanalyzer.bridge.MockSkriptBridge;
import me.glicz.skanalyzer.result.AnalyzeResults;
import me.glicz.skanalyzer.server.AnalyzerServer;
import org.bukkit.plugin.PluginLoadOrder;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.EnumSet;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.requireNonNull;

@Getter
public class SkAnalyzer {
    private final EnumSet<AnalyzerFlag> flags;
    private final Logger logger;
    private @MonotonicNonNull AnalyzerServer server;
    private boolean started;

    private SkAnalyzer(AnalyzerFlag[] flags) {
        this.flags = flags.length > 0 ? EnumSet.of(flags[0], flags) : EnumSet.noneOf(AnalyzerFlag.class);
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

            server.getPluginLoader().initPlugins();
            server.getPluginLoader().loadPlugins();

            if (flags.contains(AnalyzerFlag.FORCE_VAULT_HOOK)) {
                forceLoadHook(AnalyzerHookType.VAULT);
            }

            if (flags.contains(AnalyzerFlag.FORCE_REGIONS_HOOK)) {
                forceLoadHook(AnalyzerHookType.REGIONS);
            }

            server.getPluginLoader().enablePlugins(PluginLoadOrder.STARTUP);

            server.addSimpleWorld("world");

            server.getPluginLoader().enablePlugins(PluginLoadOrder.POSTWORLD);

            // plugins may schedule some task for server start before actual ticking starts
            server.getScheduler().performOneTick();

            future.complete(server);

            server.startTicking();
        }, "Server Thread");
        thread.setDaemon(daemon);
        thread.start();

        return future;
    }

    private void forceLoadHook(AnalyzerHookType type) {
        String name = switch (type) {
            case VAULT -> "Vault";
            case REGIONS -> "regions";
        };

        try {
            mockSkriptBridge().forceLoadHook(type);
            getLogger().info("Successfully force loaded {} hook", name);
        } catch (IOException e) {
            getLogger().error("Something went wrong while trying to force load {} hook", name, e);
        }
    }

    @Unmodifiable
    public EnumSet<AnalyzerFlag> getFlags() {
        return EnumSet.copyOf(flags);
    }

    private MockSkriptBridge mockSkriptBridge() {
        return requireNonNull(server.getServicesManager().getRegistration(MockSkriptBridge.class)).getProvider();
    }

    public CompletableFuture<AnalyzeResults> parseScript(String path) {
        return loadScript(path).whenComplete((results, ex) -> unloadScript(path));
    }

    public CompletableFuture<AnalyzeResults> loadScript(String path) {
        return mockSkriptBridge().loadScript(path);
    }

    public boolean unloadScript(String path) {
        return mockSkriptBridge().unloadScript(path);
    }

    public void unloadAllScripts() {
        mockSkriptBridge().unloadAllScripts();
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
