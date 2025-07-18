package me.glicz.skanalyzer.app.registry;

import me.glicz.skanalyzer.app.command.Command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CommandRegistry {
    private final Map<String, Command> commandMap = new HashMap<>();

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
