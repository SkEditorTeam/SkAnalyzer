package me.glicz.skanalyzer.shell.command;

import me.glicz.skanalyzer.shell.SkAnalyzerShell;

import java.io.File;

public class UnloadCommand extends Command {
    public UnloadCommand(SkAnalyzerShell app) {
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

        File file = new File(String.join(" ", args));

        if (!file.exists()) {
            app.skAnalyzer().getLogger().error("Invalid argument! Specified file does not exist: {}", file);
            return;
        }

        try {
            app.skAnalyzer().unloadScripts(file);

            app.skAnalyzer().getLogger().info("Successfully unloaded specified script(s)");
        } catch (Throwable throwable) {
            app.skAnalyzer().getLogger().atError()
                    .addArgument(file)
                    .setCause(throwable)
                    .log("Something went wrong while trying to unload '{}'");
        }
    }
}
