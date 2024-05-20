package me.glicz.skanalyzer.app.command;

import lombok.AllArgsConstructor;
import me.glicz.skanalyzer.SkAnalyzer;

@AllArgsConstructor
public class UnloadCommand implements Command {
    private SkAnalyzer skAnalyzer;

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
