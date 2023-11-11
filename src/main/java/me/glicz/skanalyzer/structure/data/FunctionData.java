package me.glicz.skanalyzer.structure.data;

public class FunctionData extends StructureData {
    private final boolean local;

    public FunctionData(int line, String value, boolean local) {
        super(line, value);
        this.local = local;
    }
}
