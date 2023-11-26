package me.glicz.skanalyzer.error;

import java.util.logging.Level;

public record ScriptError(int line, String message, Level level) {
}
