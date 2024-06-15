package me.glicz.skanalyzer.bridge.sktest;

import ch.njol.skript.log.SkriptLogger;
import me.glicz.skanalyzer.bridge.log.entry.TestLogEntry;

public interface SkTestLogger {
    String testName();

    default void testLog(String message) {
        SkriptLogger.log(new TestLogEntry(testName(), message));
    }

    default void testLog(String message, Object... args) {
        testLog(message.formatted(args));
    }
}
