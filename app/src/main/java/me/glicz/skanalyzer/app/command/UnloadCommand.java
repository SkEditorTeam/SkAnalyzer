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
            return;
        }

        String path = String.join(" ", args);

        try {
            if (app.skAnalyzer().unloadScript(path)) {
                app.skAnalyzer().getLogger().info("Successfully unloaded this script");
            }
        } catch (Throwable throwable) {
            if (throwable instanceof IllegalArgumentException e) {
                app.skAnalyzer().getLogger().atError()
                        .addArgument(e.getMessage())
                        .addArgument(path)
                        .log("Invalid argument: {} ({})");
                return;
            }

            app.skAnalyzer().getLogger().atError()
                    .addArgument(path)
                    .setCause(throwable)
                    .log("Something went wrong while trying to unload '{}'");
        }
    }
}
