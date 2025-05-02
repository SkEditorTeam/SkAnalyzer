package me.glicz.skanalyzer.app.command;

import me.glicz.skanalyzer.app.SkAnalyzerApp;
import me.glicz.skanalyzer.result.AnalyzeResults;

import java.util.concurrent.CompletableFuture;

public class ParseCommand extends AbstractParseCommand {
    public ParseCommand(SkAnalyzerApp app) {
        super(app, "parse", "Parses specified script");
    }

    @Override
    protected CompletableFuture<AnalyzeResults> parseScript(String path) {
        return app.skAnalyzer().parseScript(path);
    }
}
