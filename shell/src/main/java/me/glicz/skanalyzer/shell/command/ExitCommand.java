package me.glicz.skanalyzer.shell.command;

import me.glicz.skanalyzer.shell.SkAnalyzerShell;

public class ExitCommand extends Command {
    public ExitCommand(SkAnalyzerShell app) {
        super(app, "exit", "Exits the program");
    }

    @Override
    public void execute(String[] args) {
        System.exit(0);
    }
}
