package me.glicz.skanalyzer.result.structure.data;

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
        this.aliases = List.copyOf(aliases);
        this.permission = permission;
        this.description = description;
        this.prefix = prefix;
        this.usage = usage;
        this.arguments = List.copyOf(arguments);
    }

    public List<String> aliases() {
        return aliases;
    }

    public String permission() {
        return permission;
    }

    public String description() {
        return description;
    }

    public String prefix() {
        return prefix;
    }

    public String usage() {
        return usage;
    }

    public List<String> arguments() {
        return arguments;
    }
}
