package me.glicz.skanalyzer;

import me.glicz.skanalyzer.bridge.SkriptBridge;
import me.glicz.skanalyzer.config.Config;
import me.glicz.skanalyzer.config.provider.ConfigProvider;
import me.glicz.skanalyzer.result.AnalyzeResults;
import me.glicz.skanalyzer.server.AnalyzerServer;
import org.bukkit.plugin.PluginLoadOrder;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static com.google.common.base.Preconditions.checkState;

public final class SkAnalyzer {
    private final Logger logger;
    private final Set<File> extraPlugins;
    private final Config config;

    private @Nullable AnalyzerServer server;
    private boolean started;

    private SkAnalyzer(Builder builder) throws IOException {
        this.logger = LoggerFactory.getLogger(getClass().getSimpleName());
        this.extraPlugins = Set.copyOf(builder.extraPlugins);

        this.config = builder.configProvider.loadConfig();
    }

    @Contract(" -> new")
    public static Builder builder() {
        return new Builder();
    }

    public Logger getLogger() {
        return logger;
    }

    public Config getConfig() {
        return config;
    }

    public AnalyzerServer getServer() {
        checkState(server != null, "The server has not started yet");
        return server;
    }

    public boolean isStarted() {
        return started;
    }

    public CompletableFuture<@Nullable Void> start() {
        return start(false);
    }

    public CompletableFuture<@Nullable Void> start(boolean daemon) {
        if (started) {
            return CompletableFuture.failedFuture(new IllegalStateException());
        }

        started = true;
        logger.info("Starting...");

        return buildServer(daemon);
    }

    private CompletableFuture<@Nullable Void> buildServer(boolean daemon) {
        CompletableFuture<@Nullable Void> future = new CompletableFuture<>();

        Thread thread = new Thread(() -> {
            try {
                server = MockBukkit.mock(new AnalyzerServer(this, extraPlugins));

                server.getPluginLoader().initPlugins();
                server.getPluginLoader().loadPlugins();

                server.getPluginLoader().enablePlugins(PluginLoadOrder.STARTUP);

                server.addSimpleWorld("world");

                server.getPluginLoader().enablePlugins(PluginLoadOrder.POSTWORLD);

                // let's check if Skript bridge is present
                skriptBridge();

                try {
                    skriptBridge().forceLoadHooks(config.forcedHooks);

                    logger.info("Successfully force loaded hooks");
                } catch (IOException ex) {
                    logger.error("Something went wrong while trying to force load hooks", ex);
                }

                // plugins may schedule some task for server start before actual ticking starts
                server.getScheduler().performOneTick();

                logger.info("Successfully started. Have fun!");
                future.complete(null);

                server.startTicking();
            } catch (Throwable ex) {
                future.completeExceptionally(ex);
            }
        }, "Server Thread");
        thread.setDaemon(daemon);
        thread.start();

        return future;
    }

    private SkriptBridge skriptBridge() {
        SkriptBridge skriptBridge = getServer().getServicesManager().load(SkriptBridge.class);
        if (skriptBridge != null) {
            return skriptBridge;
        }

        getLogger().error("--------------------------------------------------------");
        getLogger().error("Required Skript bridge is missing!");
        getLogger().error("Please download a suitable Skratched Skript build from:");
        getLogger().error("https://github.com/SkEditorTeam/Skratches");
        getLogger().error("--------------------------------------------------------");

        throw new IllegalStateException("Required Skript bridge is missing!");
    }

    public CompletableFuture<AnalyzeResults> parseScripts(File... files) {
        return loadScripts(files).whenComplete((_, _) -> {
            try {
                unloadScripts(files);
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<AnalyzeResults> loadScripts(File... files) {
        return skriptBridge().loadScripts(files);
    }

    public boolean unloadScripts(File... files) throws IOException {
        return skriptBridge().unloadScripts(files);
    }

    public void unloadAllScripts() {
        skriptBridge().unloadAllScripts();
    }

    public static final class Builder {
        private final Set<File> extraPlugins = new HashSet<>();
        private ConfigProvider configProvider = Config::new;

        private Builder() {
        }

        public Builder addExtraPlugin(File plugin) {
            this.extraPlugins.add(plugin);
            return this;
        }

        public Builder addExtraPlugins(File... plugins) {
            Collections.addAll(this.extraPlugins, plugins);
            return this;
        }

        public Builder configProvider(ConfigProvider configProvider) {
            this.configProvider = configProvider;
            return this;
        }

        public SkAnalyzer build() throws IOException {
            return new SkAnalyzer(this);
        }
    }
}
