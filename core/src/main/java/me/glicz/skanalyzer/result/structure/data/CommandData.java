package me.glicz.skanalyzer.result.structure.data;

import org.jspecify.annotations.Nullable;

import java.util.List;

public record CommandData(
        int line,
        String value,
        List<String> aliases,
        @Nullable String permission,
        @Nullable String description,
        String prefix,
        @Nullable String usage,
        List<String> arguments
) implements StructureData {
    public CommandData {
        aliases = List.copyOf(aliases);
        arguments = List.copyOf(arguments);
    }
}
