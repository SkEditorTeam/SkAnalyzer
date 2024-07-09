package me.glicz.skanalyzer.structure;

import me.glicz.skanalyzer.structure.data.CommandData;
import me.glicz.skanalyzer.structure.data.EventData;
import me.glicz.skanalyzer.structure.data.FunctionData;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;

public record ScriptStructure(List<CommandData> commands, List<EventData> events,
                              List<FunctionData> functions, Map<String, String> options) {
    public @Unmodifiable List<EventData> events() {
        return List.copyOf(events);
    }

    public @Unmodifiable List<FunctionData> functions() {
        return List.copyOf(functions);
    }

    public @Unmodifiable List<CommandData> commands() {
        return List.copyOf(commands);
    }

    @Override
    public @Unmodifiable Map<String, String> options() {
        return Map.copyOf(options);
    }
}
