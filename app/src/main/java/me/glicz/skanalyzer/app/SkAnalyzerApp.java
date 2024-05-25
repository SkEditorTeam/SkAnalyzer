package me.glicz.skanalyzer.app;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.glicz.skanalyzer.AnalyzerFlag;
import me.glicz.skanalyzer.SkAnalyzer;
import me.glicz.skanalyzer.app.command.ExitCommand;
import me.glicz.skanalyzer.app.command.LoadCommand;
import me.glicz.skanalyzer.app.command.ParseCommand;
import me.glicz.skanalyzer.app.command.UnloadCommand;
import me.glicz.skanalyzer.app.registry.CommandRegistry;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

@Getter
@Accessors(fluent = true)
public class SkAnalyzerApp {
    private final SkAnalyzer skAnalyzer;
    private final CommandRegistry commandRegistry;

    public SkAnalyzerApp(String[] args) {
        System.out.printf("SkAnalyzer v%s - simple Skript parser. Created by Glicz.%n", getClass().getPackage().getSpecificationVersion());

        this.skAnalyzer = SkAnalyzer.builder()
                .flags(parseFlags(args))
                .build();

        this.commandRegistry = new CommandRegistry();
        this.commandRegistry.register(new ExitCommand(this));
        this.commandRegistry.register(new ParseCommand(this));
        this.commandRegistry.register(new LoadCommand(this));
        this.commandRegistry.register(new ParseCommand(this));
        this.commandRegistry.register(new UnloadCommand(this));

        startReadingInput();
    }

    public static void main(String[] args) {
        new SkAnalyzerApp(args);
    }

    private AnalyzerFlag[] parseFlags(String[] args) {
        return Arrays.stream(args)
                .map(AnalyzerFlag::getByArg)
                .filter(Objects::nonNull)
                .toArray(AnalyzerFlag[]::new);
    }

    private void startReadingInput() {
        Thread thread = new Thread() {
            private final Scanner scanner = new Scanner(System.in);

            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    if (scanner.hasNext()) {
                        String line = scanner.nextLine();
                        if (line != null) {
                            if (line.isBlank()) return;

                            String[] args = line.split(" ");
                            commandRegistry.getCommand(args[0]).ifPresentOrElse(
                                    command -> command.execute(Arrays.copyOfRange(args, 1, args.length)),
                                    () -> skAnalyzer.getLogger().error("Unknown command: {}", args[0])
                            );
                        }
                    }
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }
}
