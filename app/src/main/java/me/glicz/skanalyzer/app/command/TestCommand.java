package me.glicz.skanalyzer.app.command;

import me.glicz.skanalyzer.app.SkAnalyzerApp;

public class TestCommand extends Command {
    public TestCommand(SkAnalyzerApp app) {
        super(app, "test", "Runs test on loaded scripts");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            app.skAnalyzer().getLogger().error("You need to specify file path");
            return;
        }

        app.skAnalyzer().testScripts(String.join(" ", args));
    }
}
