package me.glicz.skanalyzer.mockbukkit;

import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.scheduler.BukkitSchedulerMock;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

public class AnalyzerServer extends ServerMock {
    private final AnalyzerUnsafeValues unsafe = new AnalyzerUnsafeValues();

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

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull AnalyzerUnsafeValues getUnsafe() {
        return unsafe;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull BlockData createBlockData(String data) {
        if (data.contains(":"))
            data = data.split(":")[1];
        String rawMaterial = (data.indexOf('[') == -1)
                ? data
                : data.substring(0, data.indexOf('['));
        Material material = Material.getMaterial(rawMaterial.toUpperCase());
        if (material == null)
            return null;
        return createBlockData(material);
    }

    @Override
    public @NotNull BlockData createBlockData(Material material, String data) {
        return createBlockData(material);
    }
}
