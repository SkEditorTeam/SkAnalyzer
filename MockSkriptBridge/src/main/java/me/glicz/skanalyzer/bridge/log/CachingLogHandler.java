package me.glicz.skanalyzer.bridge.log;

import ch.njol.skript.log.LogEntry;
import ch.njol.skript.log.LogHandler;
import ch.njol.skript.log.SkriptLogger;
import me.glicz.skanalyzer.result.ScriptError;

import java.io.File;
import java.util.*;

public class CachingLogHandler extends LogHandler {
    private final Map<File, List<ScriptError>> scriptErrors = new HashMap<>();

    public List<ScriptError> scriptErrors(File file) {
        return scriptErrors.containsKey(file) ? List.copyOf(scriptErrors.get(file)) : Collections.emptyList();
    }

    @Override
    public LogResult log(LogEntry entry) {
        if (entry.node == null) {
            return LogResult.CACHED;
        }

        File script = entry.node.getConfig().getFile();
        if (script == null) {
            return LogResult.CACHED;
        }

        scriptErrors.computeIfAbsent(script, $ -> new ArrayList<>())
                .add(new ScriptError(entry.node.getLine(), entry.message, entry.level));

        return LogResult.CACHED;
    }

    @Override
    public CachingLogHandler start() {
        return SkriptLogger.startLogHandler(this);
    }
}
