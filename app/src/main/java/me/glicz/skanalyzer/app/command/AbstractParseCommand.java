package me.glicz.skanalyzer.app.command;

import me.glicz.skanalyzer.app.SkAnalyzerApp;
import me.glicz.skanalyzer.result.AnalyzeResults;

import java.nio.file.InvalidPathException;
import java.util.concurrent.CompletableFuture;

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
                    if (throwable instanceof InvalidPathException e) {
                        app.skAnalyzer().getLogger().atError()
                                .addArgument(path)
                                .addArgument(e.getMessage())
                                .log("Invalid file path ('{}'): {}");
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
