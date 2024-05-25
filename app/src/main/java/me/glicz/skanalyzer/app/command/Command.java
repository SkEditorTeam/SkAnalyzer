package me.glicz.skanalyzer.app.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.glicz.skanalyzer.SkAnalyzer;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public abstract class Command {
    protected final SkAnalyzer skAnalyzer;
    protected final String name;
    protected final String description;

    public abstract void execute(String[] args);
}
