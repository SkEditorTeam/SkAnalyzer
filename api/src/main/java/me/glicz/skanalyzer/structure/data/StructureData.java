package me.glicz.skanalyzer.structure.data;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public sealed class StructureData permits CommandData, EventData, FunctionData {
    private final int line;
    private final String value;

    public StructureData(int line, String value) {
        this.line = line;
        this.value = value;
    }
}
