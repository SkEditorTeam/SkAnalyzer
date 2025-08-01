package me.glicz.skanalyzer.server;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import me.glicz.skanalyzer.SkAnalyzer;
import me.glicz.skanalyzer.plugin.loader.AnalyzerPluginLoader;
import me.glicz.skanalyzer.server.command.AnalyzerConsoleCommandSender;
import me.glicz.skanalyzer.server.potion.AnalyzerPotionBrewer;
import me.glicz.skanalyzer.server.scheduler.AnalyzerScheduler;
import me.glicz.skanalyzer.server.structure.AnalyzerStructureManager;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.data.BlockData;
import org.bukkit.loot.LootTable;
import org.bukkit.potion.PotionBrewer;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.structure.StructureManager;
import org.jspecify.annotations.Nullable;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.scheduler.paper.FoliaAsyncScheduler;
import org.mockbukkit.mockbukkit.scoreboard.CriteriaMock;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

public class AnalyzerServer extends ServerMock {
    private final AnalyzerScheduler scheduler = new AnalyzerScheduler();
    private final AnalyzerUnsafeValues unsafe = new AnalyzerUnsafeValues();
    private final AnalyzerStructureManager structureManager = new AnalyzerStructureManager();
    private final AnalyzerPotionBrewer potionBrewer = new AnalyzerPotionBrewer();
    private final FoliaAsyncScheduler asyncScheduler = new FoliaAsyncScheduler(scheduler);

    private final Logger logger = Logger.getLogger("Server");
    private @Nullable AnalyzerConsoleCommandSender consoleSender;

    private final Map<String, Criteria> scoreboardCriteria = new HashMap<>();

    private final SkAnalyzer skAnalyzer;
    private final AnalyzerPluginLoader pluginLoader;

    public AnalyzerServer(SkAnalyzer skAnalyzer) {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        this.skAnalyzer = skAnalyzer;
        this.pluginLoader = new AnalyzerPluginLoader(this);

        setPauseWhenEmptyTime(-1);
    }

    @SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
    public void startTicking() {
        try {
            while (true) {
                long start = System.currentTimeMillis();

                scheduler.performOneTick();

                long end = System.currentTimeMillis();
                long duration = end - start;

                long sleepTime = Ticks.SINGLE_TICK_DURATION_MS - duration;
                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException ex) {
                        skAnalyzer.getLogger().atError()
                                .setCause(ex)
                                .log("Something went wrong while trying to wait until next tick");
                    }
                }
            }
        } catch (Exception ex) {
            skAnalyzer.getLogger().atError()
                    .setCause(ex)
                    .log("Something went wrong while trying to tick");
        }
    }

    public AnalyzerPluginLoader getPluginLoader() {
        return pluginLoader;
    }

    @Override
    public String getName() {
        return "SkAnalyzer";
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public AnalyzerConsoleCommandSender getConsoleSender() {
        return consoleSender != null ? consoleSender : (consoleSender = new AnalyzerConsoleCommandSender());
    }

    @Override
    public AnalyzerScheduler getScheduler() {
        return scheduler;
    }

    @Override
    public Criteria getScoreboardCriteria(String name) {
        return scoreboardCriteria.computeIfAbsent(name, CriteriaMock::new);
    }

    public Map<String, Criteria> getScoreboardCriteria() {
        return Collections.unmodifiableMap(scoreboardCriteria);
    }

    @Override
    public @Nullable Advancement getAdvancement(NamespacedKey key) {
        return null;
    }

    @Override
    public Iterator<Advancement> advancementIterator() {
        return Collections.emptyIterator();
    }

    @SuppressWarnings("deprecation")
    @Override
    public AnalyzerUnsafeValues getUnsafe() {
        return unsafe;
    }

    @Override
    public BlockData createBlockData(String data) {
        String rawMaterial = (data.indexOf('[') == -1)
                ? data
                : data.substring(0, data.indexOf('['));
        Material material = Material.matchMaterial(rawMaterial);
        if (material == null) {
            throw new IllegalArgumentException();
        }

        return createBlockData(material);
    }

    @Override
    public BlockData createBlockData(Material material, String data) {
        return createBlockData(material);
    }

    @Override
    public @Nullable LootTable getLootTable(NamespacedKey key) {
        return null;
    }

    @Override
    public StructureManager getStructureManager() {
        return structureManager;
    }

    @Override
    public boolean isStopping() {
        return false;
    }

    @Override
    public PotionBrewer getPotionBrewer() {
        return potionBrewer;
    }

    @Override
    public AsyncScheduler getAsyncScheduler() {
        return asyncScheduler;
    }
}
