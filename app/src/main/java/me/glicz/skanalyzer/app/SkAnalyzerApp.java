package me.glicz.skanalyzer.app;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.glicz.skanalyzer.AnalyzerFlag;
import me.glicz.skanalyzer.SkAnalyzer;
import me.glicz.skanalyzer.app.command.*;
import me.glicz.skanalyzer.app.registry.CommandRegistry;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;

@Getter
@Accessors(fluent = true)
public class SkAnalyzerApp {
    public static final String PARENT_PROCESS_PROPERTY = "skanalyzer.parentProcess";

    private final SkAnalyzer skAnalyzer;
    private final CommandRegistry commandRegistry;

    public SkAnalyzerApp(String[] args) {
        System.out.printf("SkAnalyzer v%s - simple Skript parser. Created by Glicz.%n", getClass().getPackage().getSpecificationVersion());

        String parentProcess = System.getProperty(PARENT_PROCESS_PROPERTY);
        if (parentProcess != null) {
            try {
                long pid = Long.parseLong(parentProcess);
                ProcessHandle processHandle = ProcessHandle.of(pid).orElseThrow();
                processHandle.onExit().thenRun(() ->
                        System.exit(0)
                );
            } catch (NumberFormatException | NoSuchElementException ex) {
                System.err.printf("Invalid parent process: %s%n", parentProcess);
            }
        }

        this.skAnalyzer = SkAnalyzer.builder()
                .flags(parseFlags(args))
                .build();

        this.commandRegistry = new CommandRegistry();
        this.commandRegistry.register(new ExitCommand(this));
        this.commandRegistry.register(new HelpCommand(this));
        this.commandRegistry.register(new ParseCommand(this));
        this.commandRegistry.register(new LoadCommand(this));
        this.commandRegistry.register(new ParseCommand(this));
        this.commandRegistry.register(new UnloadCommand(this));

        this.skAnalyzer.getLogger().info("Type 'help' for help.");

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
