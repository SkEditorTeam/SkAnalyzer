package me.glicz.skanalyzer.bridge.log.entry;

import ch.njol.skript.log.LogEntry;
import ch.njol.skript.log.SkriptLogger;
import lombok.Getter;

import java.util.logging.Level;

@Getter
public class TestLogEntry extends LogEntry {
    private final String testName;

    public TestLogEntry(String testName, String message) {
        super(Level.INFO, message, SkriptLogger.getNode());
        this.testName = testName;
    }
}
