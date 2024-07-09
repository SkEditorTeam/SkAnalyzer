package me.glicz.skanalyzer.app.command;

import me.glicz.skanalyzer.app.SkAnalyzerApp;

public class HelpCommand extends Command {
    public HelpCommand(SkAnalyzerApp app) {
        super(app, "help", "Displays help");
    }

    @Override
    public void execute(String[] args) {
        app.commandRegistry().getCommands().forEach(command ->
                app.skAnalyzer().getLogger().info("{} - {}", command.name, command.description)
        );
    }
}
