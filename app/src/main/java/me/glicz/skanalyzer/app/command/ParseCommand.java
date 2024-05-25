package me.glicz.skanalyzer.app.command;

import me.glicz.skanalyzer.SkAnalyzer;

public class ParseCommand extends Command {
    public ParseCommand(SkAnalyzer skAnalyzer) {
        super(skAnalyzer, "parse", "Parses specified script");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            skAnalyzer.getLogger().error("You need to specify file path");
            return;
        }

        skAnalyzer.parseScript(args[0]).thenAccept(results ->
                skAnalyzer.getLogger().info(results.jsonResult())
        );
    }
}
