package me.glicz.skanalyzer.app.command;

import me.glicz.skanalyzer.app.SkAnalyzerApp;
import me.glicz.skanalyzer.result.AnalyzeResults;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

abstract class AbstractParseCommand extends Command {
    public AbstractParseCommand(SkAnalyzerApp app, String name, String description) {
        super(app, name, description);
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            app.skAnalyzer().getLogger().error("You need to specify file path");
            return;
        }

        String path = String.join(" ", args);

        parseScript(path)
                .thenAccept(results ->
                        app.skAnalyzer().getLogger().info(results.toString())
                )
                .exceptionally(throwable -> {
                    if (throwable instanceof CompletionException e) {
                        throwable = e.getCause();
                    }

                    if (throwable instanceof IllegalArgumentException e) {
                        app.skAnalyzer().getLogger().atError()
                                .addArgument(e.getMessage())
                                .addArgument(path)
                                .log("Invalid argument: {} ({})");
                        return null;
                    }

                    app.skAnalyzer().getLogger().atError()
                            .addArgument(path)
                            .setCause(throwable)
                            .log("Something went wrong while trying to parse '{}'");
                    return null;
                });
    }

    protected abstract CompletableFuture<AnalyzeResults> parseScript(String path);
}
