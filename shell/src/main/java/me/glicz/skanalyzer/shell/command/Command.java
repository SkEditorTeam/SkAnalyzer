package me.glicz.skanalyzer.shell.command;

import me.glicz.skanalyzer.shell.SkAnalyzerShell;

public abstract class Command {
    protected final SkAnalyzerShell app;
    protected final String name;
    protected final String description;

    public Command(SkAnalyzerShell app, String name, String description) {
        this.app = app;
        this.name = name;
        this.description = description;
    }

    public final String name() {
        return name;
    }

    public final String description() {
        return description;
    }

    public abstract void execute(String[] args);
}
