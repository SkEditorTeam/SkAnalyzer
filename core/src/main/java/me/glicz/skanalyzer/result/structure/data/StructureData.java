package me.glicz.skanalyzer.result.structure.data;

public sealed interface StructureData permits CommandData, EventData, FunctionData {
    int line();

    String value();
}
