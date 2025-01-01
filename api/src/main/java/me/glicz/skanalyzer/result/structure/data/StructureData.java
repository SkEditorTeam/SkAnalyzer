package me.glicz.skanalyzer.result.structure.data;

public abstract sealed class StructureData permits CommandData, EventData, FunctionData {
    private final int line;
    private final String value;

    public StructureData(int line, String value) {
        this.line = line;
        this.value = value;
    }

    public int line() {
        return line;
    }

    public String value() {
        return value;
    }
}
