package me.glicz.skanalyzer.shell.command;

import me.glicz.skanalyzer.shell.SkAnalyzerShell;
import me.glicz.skanalyzer.result.AnalyzeResults;

import java.util.concurrent.CompletableFuture;

public class LoadCommand extends AbstractParseCommand {
    public LoadCommand(SkAnalyzerShell app) {
        super(app, "load", "Loads specified script(s)");
    }

    @Override
    protected CompletableFuture<AnalyzeResults> parseScript(String path) {
        return app.skAnalyzer().loadScript(path);
    }
}
