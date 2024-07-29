package me.glicz.skanalyzer.util;

import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public record Message(Identity source, Component value, Type type) {
    public String rawValue() {
        return PlainTextComponentSerializer.plainText().serialize(value);
    }

    public enum Type {
        CHAT,
        SYSTEM
    }
}
