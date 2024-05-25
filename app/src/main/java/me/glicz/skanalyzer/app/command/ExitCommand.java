package me.glicz.skanalyzer.app.command;

import me.glicz.skanalyzer.app.SkAnalyzerApp;

public class ExitCommand extends Command {
    public ExitCommand(SkAnalyzerApp app) {
        super(app, "exit", "Exits the program");
    }

    @Override
    public void execute(String[] args) {
        System.exit(0);
    }
}
