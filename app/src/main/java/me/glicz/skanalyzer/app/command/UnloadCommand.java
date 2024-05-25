package me.glicz.skanalyzer.app.command;

import me.glicz.skanalyzer.SkAnalyzer;

public class UnloadCommand extends Command {
    public UnloadCommand(SkAnalyzer skAnalyzer) {
        super(skAnalyzer, "unload", "Unloads specified script(s)");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            skAnalyzer.getLogger().error("You need to specify file path");
            return;
        }

        if (args[0].equals("*")) {
            skAnalyzer.unloadAllScripts();
            skAnalyzer.getLogger().info("Successfully unloaded all scripts");
        } else {
            if (skAnalyzer.unloadScript(args[0])) {
                skAnalyzer.getLogger().info("Successfully unloaded this script");
            }
        }
    }
}
