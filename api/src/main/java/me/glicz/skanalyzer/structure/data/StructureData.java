package me.glicz.skanalyzer.structure.data;

public sealed class StructureData permits EventData, FunctionData {
    private final int line;
    private final String value;

    public StructureData(int line, String value) {
        this.line = line;
        this.value = value;
    }
}
