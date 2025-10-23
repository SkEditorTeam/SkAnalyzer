package me.glicz.skanalyzer.shell.command;

import me.glicz.skanalyzer.shell.SkAnalyzerShell;
import me.glicz.skanalyzer.result.AnalyzeResults;

import java.util.concurrent.CompletableFuture;

public class ParseCommand extends AbstractParseCommand {
    public ParseCommand(SkAnalyzerShell app) {
        super(app, "parse", "Parses specified script");
    }

    @Override
    protected CompletableFuture<AnalyzeResults> parseScript(String path) {
        return app.skAnalyzer().parseScript(path);
    }
}
