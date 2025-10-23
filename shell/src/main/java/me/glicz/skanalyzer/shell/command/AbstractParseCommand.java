package me.glicz.skanalyzer.shell.command;

import me.glicz.skanalyzer.result.AnalyzeResults;
import me.glicz.skanalyzer.shell.SkAnalyzerShell;
import me.glicz.skanalyzer.shell.util.serialize.Serialization;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

abstract class AbstractParseCommand extends Command {
    public AbstractParseCommand(SkAnalyzerShell app, String name, String description) {
        super(app, name, description);
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            app.skAnalyzer().getLogger().error("You need to specify file path");
            return;
        }

        File file = new File(String.join(" ", args));

        if (!file.exists()) {
            app.skAnalyzer().getLogger().error("Invalid argument! Specified file does not exist: {}", file);
            return;
        }

        parseScript(file)
                .thenAccept(results ->
                        app.skAnalyzer().getLogger().info(Serialization.GSON.toJson(results))
                )
                .exceptionally(throwable -> {
                    if (throwable instanceof CompletionException e) {
                        throwable = e.getCause();
                    }

                    app.skAnalyzer().getLogger().atError()
                            .addArgument(file)
                            .setCause(throwable)
                            .log("Something went wrong while trying to parse '{}'");

                    return null;
                });
    }

    protected abstract CompletableFuture<AnalyzeResults> parseScript(File file);
}
