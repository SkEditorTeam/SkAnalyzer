package me.glicz.skanalyzer.structure;

import me.glicz.skanalyzer.structure.data.CommandData;
import me.glicz.skanalyzer.structure.data.EventData;
import me.glicz.skanalyzer.structure.data.FunctionData;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public record ScriptStructure(List<CommandData> commandDataList, List<EventData> eventDataList,
                              List<FunctionData> functionDataList, Map<String, String> options) {
    @Override
    public @Unmodifiable List<EventData> eventDataList() {
        return Collections.unmodifiableList(eventDataList);
    }

    @Override
    public @Unmodifiable List<FunctionData> functionDataList() {
        return Collections.unmodifiableList(functionDataList);
    }

    @Override
    public @Unmodifiable List<CommandData> commandDataList() {
        return Collections.unmodifiableList(commandDataList);
    }

    @Override
    public @Unmodifiable Map<String, String> options() {
        return Collections.unmodifiableMap(options);
    }
}
