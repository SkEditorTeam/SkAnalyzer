package me.glicz.skanalyzer.result.structure.data;

import org.bukkit.event.EventPriority;

public final class EventData extends StructureData {
    private final String id;
    private final EventPriority eventPriority;

    public EventData(int line, String value, String id, EventPriority eventPriority) {
        super(line, value);
        this.id = id;
        this.eventPriority = eventPriority;
    }

    public String id() {
        return id;
    }

    public EventPriority eventPriority() {
        return eventPriority;
    }
}
