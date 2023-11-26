package me.glicz.skanalyzer.structure.data;

import java.util.List;

public final class CommandData extends StructureData {
    private final List<String> aliases;
    private final String permission;
    private final String description;
    private final String prefix;
    private final String usage;
    private final List<String> arguments;

    public CommandData(int line, String value, List<String> aliases, String permission,
                       String description, String prefix, String usage, List<String> arguments) {
        super(line, value);
        this.aliases = aliases;
        this.permission = permission;
        this.description = description;
        this.prefix = prefix;
        this.usage = usage;
        this.arguments = arguments;
    }
}
