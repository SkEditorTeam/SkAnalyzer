package me.glicz.skanalyzer.shell.command;

import me.glicz.skanalyzer.result.AnalyzeResults;
import me.glicz.skanalyzer.shell.SkAnalyzerShell;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class LoadCommand extends AbstractParseCommand {
    public LoadCommand(SkAnalyzerShell app) {
        super(app, "load", "Loads specified script(s)");
    }

    @Override
    protected CompletableFuture<AnalyzeResults> parseScript(File file) {
        return app.skAnalyzer().loadScripts(file);
    }
}
