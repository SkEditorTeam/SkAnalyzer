package me.glicz.skanalyzer.mockbukkit;

import lombok.Getter;
import me.glicz.skanalyzer.SkAnalyzer;
import me.glicz.skanalyzer.loader.AddonsLoader;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.scheduler.BukkitSchedulerMock;
import org.mockbukkit.mockbukkit.scheduler.paper.FoliaAsyncScheduler;

@Getter
public class AnalyzerServer extends ServerMock {
    private final AnalyzerPotionBrewer potionBrewer = new AnalyzerPotionBrewer();
    private final AnalyzerScheduler scheduler = new AnalyzerScheduler();
    private final FoliaAsyncScheduler foliaAsyncScheduler = new FoliaAsyncScheduler(scheduler);
    private final AnalyzerStructureManager structureManager = new AnalyzerStructureManager();
    private final AnalyzerUnsafeValues unsafe = new AnalyzerUnsafeValues();
    private final SkAnalyzer skAnalyzer;
    private final AddonsLoader addonsLoader;

    public AnalyzerServer(SkAnalyzer skAnalyzer) {
        this.skAnalyzer = skAnalyzer;
        this.addonsLoader = new AddonsLoader(skAnalyzer, this);
    }

    @SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
    public void startTicking() {
        try {
            while (true) {
                long start = System.currentTimeMillis();

                ((BukkitSchedulerMock) Bukkit.getScheduler()).performOneTick();

                long end = System.currentTimeMillis();
                long duration = end - start;

                long sleepTime = Ticks.SINGLE_TICK_DURATION_MS - duration;
                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException ex) {
                        skAnalyzer.getLogger().atError()
                                .withThrowable(ex)
                                .log("Something went wrong while trying to wait until next tick");
                    }
                }
            }
        } catch (Exception ex) {
            skAnalyzer.getLogger().atError()
                    .withThrowable(ex)
                    .log("Something went wrong while trying to tick");
        }
    }

    @Override
    public @NotNull String getName() {
        return "SkAnalyzer";
    }

    @Override
    public @NotNull BlockData createBlockData(String data) {
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
    public @NotNull BlockData createBlockData(Material material, String data) {
        return createBlockData(material);
    }

    @Override
    public boolean isStopping() {
        return false;
    }
}
