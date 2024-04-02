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
import java.util.stream.Collectors;

public class CachingLogHandler extends LogHandler {
    private final Map<File, List<ScriptError>> scriptErrors = new HashMap<>();

    public @Unmodifiable Map<File, List<ScriptError>> scriptErrors() {
        return Map.copyOf(scriptErrors.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> List.copyOf(entry.getValue())
        )));
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
