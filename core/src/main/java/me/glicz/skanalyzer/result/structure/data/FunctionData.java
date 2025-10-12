package me.glicz.skanalyzer.result.structure.data;

import org.jspecify.annotations.Nullable;

import java.util.Map;

public final class FunctionData extends StructureData {
    private final boolean local;
    private final Map<String, String> parameters;
    private final @Nullable String returnType;

    public FunctionData(int line, String value, boolean local, Map<String, String> parameters, @Nullable String returnType) {
        super(line, value);
        this.local = local;
        this.parameters = Map.copyOf(parameters);
        this.returnType = returnType;
    }

    public boolean local() {
        return local;
    }

    public Map<String, String> parameters() {
        return parameters;
    }

    public @Nullable String returnType() {
        return returnType;
    }
}
