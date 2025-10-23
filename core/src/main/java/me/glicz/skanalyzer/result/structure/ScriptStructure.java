package me.glicz.skanalyzer.result.structure;

import me.glicz.skanalyzer.result.structure.data.CommandData;
import me.glicz.skanalyzer.result.structure.data.EventData;
import me.glicz.skanalyzer.result.structure.data.FunctionData;

import java.util.List;
import java.util.Map;

public record ScriptStructure(
        List<CommandData> commands,
        List<EventData> events,
        List<FunctionData> functions,
        Map<String, String> options
) {
    public ScriptStructure {
        commands = List.copyOf(commands);
        events = List.copyOf(events);
        functions = List.copyOf(functions);
        options = Map.copyOf(options);
    }
}
