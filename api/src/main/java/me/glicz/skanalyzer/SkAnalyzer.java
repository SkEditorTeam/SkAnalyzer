package me.glicz.skanalyzer;

import me.glicz.skanalyzer.bridge.MockSkriptBridge;
import me.glicz.skanalyzer.result.AnalyzeResults;
import me.glicz.skanalyzer.server.AnalyzerServer;
import me.glicz.skanalyzer.util.EnumSets;
import org.bukkit.plugin.PluginLoadOrder;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;

public final class SkAnalyzer {
    private final EnumSet<AnalyzerFlag> flags;
    private final Set<File> extraPlugins;
    private final Logger logger;

    private @MonotonicNonNull AnalyzerServer server;
    private boolean started;

    private SkAnalyzer(Builder builder) {
        this.flags = EnumSets.of(AnalyzerFlag.class, builder.flags);
        this.extraPlugins = Set.copyOf(builder.extraPlugins);
        this.logger = LoggerFactory.getLogger(getClass().getSimpleName());
    }

    @Contract(" -> new")
    public static Builder builder() {
        return new Builder();
    }

    @Unmodifiable
    public Set<AnalyzerFlag> getFlags() {
        return unmodifiableSet(flags);
    }

    public Logger getLogger() {
        return logger;
    }

    public AnalyzerServer getServer() {
        return server;
    }

    public boolean isStarted() {
        return started;
    }

    public CompletableFuture<AnalyzerServer> start() {
        return start(false);
    }

    public CompletableFuture<AnalyzerServer> start(boolean daemon) {
        if (started) {
            return CompletableFuture.failedFuture(new IllegalStateException());
        }

        started = true;
        logger.info("Enabling...");

        return buildServer(daemon);
    }

    private CompletableFuture<AnalyzerServer> buildServer(boolean daemon) {
        CompletableFuture<AnalyzerServer> future = new CompletableFuture<>();

        Thread thread = new Thread(() -> {
            server = MockBukkit.mock(new AnalyzerServer(this, extraPlugins));

            server.getPluginLoader().initPlugins();
            server.getPluginLoader().loadPlugins();

            server.getPluginLoader().enablePlugins(PluginLoadOrder.STARTUP);

            server.addSimpleWorld("world");

            server.getPluginLoader().enablePlugins(PluginLoadOrder.POSTWORLD);

            if (flags.contains(AnalyzerFlag.FORCE_VAULT_HOOK)) {
                forceLoadHook(AnalyzerHookType.VAULT);
            }

            if (flags.contains(AnalyzerFlag.FORCE_REGIONS_HOOK)) {
                forceLoadHook(AnalyzerHookType.REGIONS);
            }

            // plugins may schedule some task for server start before actual ticking starts
            server.getScheduler().performOneTick();

            logger.info("Successfully enabled. Have fun!");
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

    public static final class Builder {
        private AnalyzerFlag[] flags = {};
        private final Set<File> extraPlugins = new HashSet<>();

        private Builder() {
        }

        public Builder flags(AnalyzerFlag... flags) {
            this.flags = flags;
            return this;
        }

        public Builder addPlugin(File plugin) {
            this.extraPlugins.add(plugin);
            return this;
        }

        public Builder addPlugins(File... plugins) {
            Collections.addAll(this.extraPlugins, plugins);
            return this;
        }

        public SkAnalyzer build() {
            return new SkAnalyzer(this);
        }
    }
}
