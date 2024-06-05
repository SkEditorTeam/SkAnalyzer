package me.glicz.skanalyzer.mockbukkit;

import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AnalyzerPlayer extends PlayerMock {
    AnalyzerPlayer(@NotNull ServerMock server, @NotNull String name, @NotNull UUID uuid) {
        super(server, name, uuid);
    }

    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    @Override
    public void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
        super.sendMessage(source, message, type);

        System.out.println(getName() + " <- " + type.name() + ": " + PlainTextComponentSerializer.plainText().serialize(message));
    }
}
