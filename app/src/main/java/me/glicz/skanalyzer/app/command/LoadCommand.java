package me.glicz.skanalyzer.app.command;

import me.glicz.skanalyzer.app.SkAnalyzerApp;
import me.glicz.skanalyzer.result.AnalyzeResults;

import java.util.concurrent.CompletableFuture;

public class LoadCommand extends AbstractParseCommand {
    public LoadCommand(SkAnalyzerApp app) {
        super(app, "load", "Loads specified script(s)");
    }

    @Override
    protected CompletableFuture<AnalyzeResults> parseScript(String path) {
        return app.skAnalyzer().loadScript(path);
    }
}
