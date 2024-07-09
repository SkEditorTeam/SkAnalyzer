package me.glicz.skanalyzer.app.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.glicz.skanalyzer.app.SkAnalyzerApp;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public abstract class Command {
    protected final SkAnalyzerApp app;
    protected final String name;
    protected final String description;

    public abstract void execute(String[] args);
}
