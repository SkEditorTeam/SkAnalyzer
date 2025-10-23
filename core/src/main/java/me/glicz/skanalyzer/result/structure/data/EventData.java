package me.glicz.skanalyzer.result.structure.data;

import org.bukkit.event.EventPriority;

public record EventData(
        int line,
        String value,
        String id,
        EventPriority eventPriority
) implements StructureData {
}
