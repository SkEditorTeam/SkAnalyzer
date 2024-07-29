package me.glicz.skanalyzer.bridge.log;

import ch.njol.skript.log.LogEntry;
import ch.njol.skript.log.SkriptLogger;
import me.glicz.skanalyzer.bridge.log.entry.TestLogEntry;
import org.jetbrains.annotations.NotNull;

public class TestLogHandler extends CachingLogHandler {
    @Override
    public @NotNull LogResult log(@NotNull LogEntry entry) {
        if (entry instanceof TestLogEntry testLogEntry) {
            System.out.printf("Test \"%s\": %s%n", testLogEntry.getTestName(), testLogEntry.message);
            return LogResult.CACHED;
        }

        return super.log(entry);
    }

    @Override
    public @NotNull TestLogHandler start() {
        return SkriptLogger.startLogHandler(this);
    }
}
