package me.glicz.skanalyzer.structure.data;

import org.bukkit.event.EventPriority;

public class EventData extends StructureData {
    private final String id;
    private final EventPriority eventPriority;

    public EventData(int line, String value, String id, EventPriority eventPriority) {
        super(line, value);
        this.id = id;
        this.eventPriority = eventPriority;
    }
}
