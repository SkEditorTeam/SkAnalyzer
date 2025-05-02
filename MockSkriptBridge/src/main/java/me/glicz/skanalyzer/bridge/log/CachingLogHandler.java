package me.glicz.skanalyzer.bridge.log;

import ch.njol.skript.log.LogEntry;
import ch.njol.skript.log.LogHandler;
import ch.njol.skript.log.SkriptLogger;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import me.glicz.skanalyzer.result.ScriptError;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class CachingLogHandler extends LogHandler {
    private final Multimap<File, ScriptError> scriptErrors = MultimapBuilder.hashKeys().arrayListValues().build();

    public List<ScriptError> getScriptErrors(File file) {
        return scriptErrors.containsKey(file) ? List.copyOf(scriptErrors.get(file)) : Collections.emptyList();
    }

    @Override
    public LogResult log(LogEntry entry) {
        if (entry.node == null) {
            return LogResult.CACHED;
        }

        File file = entry.node.getConfig().getFile();
        if (file == null) {
            return LogResult.CACHED;
        }

        scriptErrors.put(file, new ScriptError(
                entry.node.getLine(), entry.message, entry.level
        ));
        return LogResult.CACHED;
    }

    @Override
    public CachingLogHandler start() {
        return SkriptLogger.startLogHandler(this);
    }
}
