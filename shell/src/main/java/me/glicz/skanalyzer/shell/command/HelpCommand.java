package me.glicz.skanalyzer.shell.command;

import me.glicz.skanalyzer.shell.SkAnalyzerShell;

public class HelpCommand extends Command {
    public HelpCommand(SkAnalyzerShell app) {
        super(app, "help", "Displays help");
    }

    @Override
    public void execute(String[] args) {
        app.commandRegistry().getCommands().forEach(command ->
                app.skAnalyzer().getLogger().info("{} - {}", command.name, command.description)
        );
    }
}
