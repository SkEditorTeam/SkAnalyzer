package me.glicz.skanalyzer.mockbukkit;

import be.seeseemelk.mockbukkit.scheduler.BukkitSchedulerMock;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class AnalyzerScheduler extends BukkitSchedulerMock {
    @Override
    public synchronized @NotNull <T> Future<T> callSyncMethod(@NotNull Plugin plugin, @NotNull Callable<T> task) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return task.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, getMainThreadExecutor(plugin));
    }
}
