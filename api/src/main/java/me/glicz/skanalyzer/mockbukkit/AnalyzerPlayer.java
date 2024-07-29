package me.glicz.skanalyzer.mockbukkit;

import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import me.glicz.skanalyzer.util.Message;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.BiConsumer;

public class AnalyzerPlayer extends PlayerMock {
    private final BiConsumer<AnalyzerPlayer, Message> messageHandler;

    AnalyzerPlayer(@NotNull ServerMock server, @NotNull String name, @NotNull UUID uuid, BiConsumer<AnalyzerPlayer, Message> messageHandler) {
        super(server, name, uuid);
        this.messageHandler = messageHandler;
    }

    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    @Override
    public void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
        super.sendMessage(source, message, type);

        messageHandler.accept(this, new Message(source, message, Message.Type.valueOf(type.name())));
    }
}
