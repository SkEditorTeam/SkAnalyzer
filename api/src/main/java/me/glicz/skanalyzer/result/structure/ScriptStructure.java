package me.glicz.skanalyzer.result.structure;

import me.glicz.skanalyzer.result.structure.data.CommandData;
import me.glicz.skanalyzer.result.structure.data.EventData;
import me.glicz.skanalyzer.result.structure.data.FunctionData;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record ScriptStructure(
        List<CommandData> commands,
        List<EventData> events,
        List<FunctionData> functions,
        Map<String, String> options,
        Set<String> usedAddons
) {
    public ScriptStructure(
            List<CommandData> commands,
            List<EventData> events,
            List<FunctionData> functions,
            Map<String, String> options,
            Set<String> usedAddons
    ) {
        this.commands = List.copyOf(commands);
        this.events = List.copyOf(events);
        this.functions = List.copyOf(functions);
        this.options = Map.copyOf(options);
        this.usedAddons = Set.copyOf(usedAddons);
    }
}
