package me.glicz.skanalyzer.app.command;

import me.glicz.skanalyzer.app.SkAnalyzerApp;

public class UnloadCommand extends Command {
    public UnloadCommand(SkAnalyzerApp app) {
        super(app, "unload", "Unloads specified script(s)");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            app.skAnalyzer().getLogger().error("You need to specify file path");
            return;
        }

        if (args[0].equals("*")) {
            app.skAnalyzer().unloadAllScripts();
            app.skAnalyzer().getLogger().info("Successfully unloaded all scripts");
        } else {
            if (app.skAnalyzer().unloadScript(String.join(" ", args))) {
                app.skAnalyzer().getLogger().info("Successfully unloaded this script");
            }
        }
    }
}
