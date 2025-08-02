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
    public ScriptStructure(
            List<CommandData> commands,
            List<EventData> events,
            List<FunctionData> functions,
            Map<String, String> options
    ) {
        this.commands = List.copyOf(commands);
        this.events = List.copyOf(events);
        this.functions = List.copyOf(functions);
        this.options = Map.copyOf(options);
    }
}
