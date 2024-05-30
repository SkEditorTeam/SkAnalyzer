package me.glicz.skanalyzer.mockbukkit;

import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.scheduler.BukkitSchedulerMock;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

@Getter
public class AnalyzerServer extends ServerMock {
    private final AnalyzerUnsafeValues unsafe = new AnalyzerUnsafeValues();
    private final AnalyzerStructureManager structureManager = new AnalyzerStructureManager();
    private final AnalyzerPotionBrewer potionBrewer = new AnalyzerPotionBrewer();

    public void startTicking() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ((BukkitSchedulerMock) Bukkit.getScheduler()).performOneTick();
            }
        }, 50, 50);
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
