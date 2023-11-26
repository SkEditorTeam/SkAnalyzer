package me.glicz.skanalyzer.structure;

import me.glicz.skanalyzer.structure.data.EventData;
import me.glicz.skanalyzer.structure.data.FunctionData;
import me.glicz.skanalyzer.structure.data.StructureData;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;

public record ScriptStructure(List<StructureData> commandDataList, List<EventData> eventDataList,
                              List<FunctionData> functionDataList) {
    @Override
    @Unmodifiable
    public List<EventData> eventDataList() {
        return Collections.unmodifiableList(eventDataList);
    }

    @Override
    @Unmodifiable
    public List<FunctionData> functionDataList() {
        return Collections.unmodifiableList(functionDataList);
    }

    @Override
    @Unmodifiable
    public List<StructureData> commandDataList() {
        return Collections.unmodifiableList(commandDataList);
    }
}
