package me.glicz.skanalyzer.app.command;

import me.glicz.skanalyzer.app.SkAnalyzerApp;

public class LoadCommand extends Command {
    public LoadCommand(SkAnalyzerApp app) {
        super(app, "load", "Loads specified script(s)");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            app.skAnalyzer().getLogger().error("You need to specify file path");
            return;
        }

        app.skAnalyzer().parseScript(String.join(" ", args), true).thenAccept(results ->
                app.skAnalyzer().getLogger().info(results.jsonResult())
        );
    }
}
