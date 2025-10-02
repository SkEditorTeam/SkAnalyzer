package me.glicz.skanalyzer.app.command;

import me.glicz.skanalyzer.app.SkAnalyzerApp;

public abstract class Command {
    protected final SkAnalyzerApp app;
    protected final String name;
    protected final String description;

    public Command(SkAnalyzerApp app, String name, String description) {
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
