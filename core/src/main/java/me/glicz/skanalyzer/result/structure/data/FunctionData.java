package me.glicz.skanalyzer.result.structure.data;

import org.jspecify.annotations.Nullable;

import java.util.Map;

public record FunctionData(
        int line,
        String value,
        boolean local,
        Map<String, String> parameters,
        @Nullable String returnType
) implements StructureData {
    public FunctionData {
        parameters = Map.copyOf(parameters);
    }
}
