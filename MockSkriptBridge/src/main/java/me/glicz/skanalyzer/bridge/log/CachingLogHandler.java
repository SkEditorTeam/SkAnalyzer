package me.glicz.skanalyzer.bridge.log;

import ch.njol.skript.log.LogEntry;
import ch.njol.skript.log.LogHandler;
import ch.njol.skript.log.SkriptLogger;
import me.glicz.skanalyzer.error.ScriptError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CachingLogHandler extends LogHandler {
    private final Map<File, List<ScriptError>> scriptErrors = new HashMap<>();

    public @Unmodifiable List<ScriptError> scriptErrors(File file) {
        if (scriptErrors.containsKey(file)) {
            return List.copyOf(scriptErrors.get(file));
        }
        return List.of();
    }

    @Override
    public @NotNull LogResult log(@NotNull LogEntry entry) {
        if (entry.node != null) {
            this.scriptErrors.computeIfAbsent(entry.node.getConfig().getFile(), file -> new ArrayList<>())
                    .add(new ScriptError(entry.node.getLine(), entry.message, entry.level));
        }

        return LogResult.CACHED;
    }

    @Override
    public @NotNull CachingLogHandler start() {
        return SkriptLogger.startLogHandler(this);
    }
}
