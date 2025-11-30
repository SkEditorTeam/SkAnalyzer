package me.glicz.skanalyzer.shell.registry;

import me.glicz.skanalyzer.shell.command.Command;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CommandRegistry {
    private final Map<String, Command> commandMap = new ConcurrentHashMap<>();

    public void register(Command command) {
        commandMap.put(command.name(), command);
    }

    public Optional<Command> getCommand(String name) {
        return Optional.ofNullable(commandMap.get(name));
    }

    public List<Command> getCommands() {
        return List.copyOf(commandMap.values());
    }
}
