package me.glicz.skanalyzer.structure.data;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Map;

@Getter
@Accessors(fluent = true)
public final class FunctionData extends StructureData {
    private final boolean local;
    private final Map<String, String> parameters;
    private final String returnType;

    public FunctionData(int line, String value, boolean local, Map<String, String> parameters, String returnType) {
        super(line, value);
        this.local = local;
        this.parameters = parameters;
        this.returnType = returnType;
    }
}
