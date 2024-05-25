package me.glicz.skanalyzer.app.command;

import me.glicz.skanalyzer.SkAnalyzer;

public class ExitCommand extends Command {
    public ExitCommand(SkAnalyzer skAnalyzer) {
        super(skAnalyzer, "exit", "Exits the program");
    }

    @Override
    public void execute(String[] args) {
        System.exit(0);
    }
}
