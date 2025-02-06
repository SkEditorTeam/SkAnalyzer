package me.glicz.skanalyzer.app.command;

import me.glicz.skanalyzer.app.SkAnalyzerApp;

import java.nio.file.InvalidPathException;

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
            return;
        }

        String path = String.join(" ", args);

        try {
            if (app.skAnalyzer().unloadScript(path)) {
                app.skAnalyzer().getLogger().info("Successfully unloaded this script");
            }
        } catch (InvalidPathException e) {
            app.skAnalyzer().getLogger().atError()
                    .addArgument(path)
                    .addArgument(e.getMessage())
                    .log("Invalid file path ('{}'): {}");
        }
    }
}
