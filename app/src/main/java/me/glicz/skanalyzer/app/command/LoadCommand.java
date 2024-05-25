package me.glicz.skanalyzer.app.command;

import me.glicz.skanalyzer.SkAnalyzer;

public class LoadCommand extends Command {
    public LoadCommand(SkAnalyzer skAnalyzer) {
        super(skAnalyzer, "load", "Loads specified script(s)");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            skAnalyzer.getLogger().error("You need to specify file path");
            return;
        }

        skAnalyzer.parseScript(args[0], true).thenAccept(results ->
                skAnalyzer.getLogger().info(results.jsonResult())
        );
    }
}
