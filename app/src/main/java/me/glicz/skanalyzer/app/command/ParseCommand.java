package me.glicz.skanalyzer.app.command;

import lombok.AllArgsConstructor;
import me.glicz.skanalyzer.SkAnalyzer;

@AllArgsConstructor
public class ParseCommand implements Command {
    private final SkAnalyzer skAnalyzer;

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
