package me.glicz.skanalyzer.app;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.glicz.skanalyzer.AnalyzerFlag;
import me.glicz.skanalyzer.SkAnalyzer;
import me.glicz.skanalyzer.app.command.*;
import me.glicz.skanalyzer.app.registry.CommandRegistry;

import java.io.File;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;

@Getter
@Accessors(fluent = true)
public class SkAnalyzerApp {
    private static final String PARENT_PROCESS_PROPERTY = "skanalyzer.parentProcess";

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

        OptionParser optionParser = new OptionParser();

        OptionSpec<File> addPluginSpec = optionParser.accepts("add-plugin")
                .withRequiredArg()
                .ofType(File.class);

        optionParser.allowsUnrecognizedOptions();

        OptionSet optionSet = optionParser.parse(args);

        this.skAnalyzer = SkAnalyzer.builder()
                .flags(parseFlags(args)) // TODO use joptsimple
                .addPlugins(optionSet.valuesOf(addPluginSpec).toArray(File[]::new))
                .build();
        this.commandRegistry = new CommandRegistry();

        this.skAnalyzer.start().thenRun(() -> {
            this.commandRegistry.register(new ExitCommand(this));
            this.commandRegistry.register(new HelpCommand(this));
            this.commandRegistry.register(new ParseCommand(this));
            this.commandRegistry.register(new LoadCommand(this));
            this.commandRegistry.register(new ParseCommand(this));
            this.commandRegistry.register(new UnloadCommand(this));

            this.skAnalyzer.getLogger().info("Type 'help' for help.");

            startReadingInput();
        });
    }

    public static void main(String[] args) {
        new SkAnalyzerApp(args);
    }

    private static AnalyzerFlag[] parseFlags(String[] args) {
        return Arrays.stream(args).map(AnalyzerFlag::getByArg).filter(Objects::nonNull).toArray(AnalyzerFlag[]::new);
    }

    private void startReadingInput() {
        Thread thread = new Thread("Command Input Thread") {
            private final Scanner scanner = new Scanner(System.in);

            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    try {
                        if (!scanner.hasNext()) {
                            continue;
                        }

                        String line = scanner.nextLine();
                        if (line == null || line.isBlank()) {
                            continue;
                        }

                        String[] args = line.split(" ");
                        commandRegistry.getCommand(args[0]).ifPresentOrElse(
                                command -> command.execute(Arrays.copyOfRange(args, 1, args.length)),
                                () -> skAnalyzer.getLogger().error("Unknown command: {}", args[0])
                        );
                    } catch (Exception e) {
                        skAnalyzer.getLogger().atError()
                                .addArgument(Thread.currentThread())
                                .setCause(e)
                                .log("An exception occurred in {}. You should report this issue immediately.");
                    }
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }
}
