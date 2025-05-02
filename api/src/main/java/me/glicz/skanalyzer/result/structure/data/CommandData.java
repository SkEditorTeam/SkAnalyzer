package me.glicz.skanalyzer.result.structure.data;

import org.jspecify.annotations.Nullable;

import java.util.List;

public final class CommandData extends StructureData {
    private final List<String> aliases;
    private final @Nullable String permission;
    private final @Nullable String description;
    private final String prefix;
    private final @Nullable String usage;
    private final List<String> arguments;

    public CommandData(
            int line,
            String value,
            List<String> aliases,
            @Nullable String permission,
            @Nullable String description,
            String prefix,
            @Nullable String usage,
            List<String> arguments
    ) {
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

    public @Nullable String permission() {
        return permission;
    }

    public @Nullable String description() {
        return description;
    }

    public String prefix() {
        return prefix;
    }

    public @Nullable String usage() {
        return usage;
    }

    public List<String> arguments() {
        return arguments;
    }
}
