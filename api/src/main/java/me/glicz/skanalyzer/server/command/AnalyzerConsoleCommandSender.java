package me.glicz.skanalyzer.server.command;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mockbukkit.mockbukkit.command.ConsoleCommandSenderMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class AnalyzerConsoleCommandSender extends ConsoleCommandSenderMock {
    private static final Logger LOGGER = LoggerFactory.getLogger("");

    @Override
    public void sendRawMessage(@Nullable UUID sender, @NotNull String message) {
        // noinspection deprecation
        LOGGER.info(ChatColor.stripColor(message));

        super.sendRawMessage(sender, message);
    }
}
