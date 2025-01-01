package me.glicz.skanalyzer.server.scheduler;

import org.bukkit.plugin.Plugin;
import org.mockbukkit.mockbukkit.scheduler.BukkitSchedulerMock;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class AnalyzerScheduler extends BukkitSchedulerMock {
    @Override
    public synchronized <T> Future<T> callSyncMethod(Plugin plugin, Callable<T> task) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return task.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, getMainThreadExecutor(plugin));
    }
}
