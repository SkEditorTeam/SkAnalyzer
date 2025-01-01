package me.glicz.skanalyzer.app.command;

import me.glicz.skanalyzer.app.SkAnalyzerApp;

public class ParseCommand extends Command {
    public ParseCommand(SkAnalyzerApp app) {
        super(app, "parse", "Parses specified script");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            app.skAnalyzer().getLogger().error("You need to specify file path");
            return;
        }

        app.skAnalyzer().parseScript(String.join(" ", args)).thenAccept(results ->
                app.skAnalyzer().getLogger().info(results.toString())
        );
    }
}
