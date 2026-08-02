package me.glicz.skanalyzer.shell;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import me.glicz.skanalyzer.SkAnalyzer;
import me.glicz.skanalyzer.config.provider.configurate.YamlConfigurateConfigProvider;
import me.glicz.skanalyzer.shell.command.*;
import me.glicz.skanalyzer.shell.registry.CommandRegistry;
import me.glicz.skanalyzer.shell.util.CommandInputHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.NoSuchElementException;

public class SkAnalyzerShell {
    private static final String PARENT_PROCESS_PROPERTY = "skanalyzer.parentProcess";

    private final SkAnalyzer skAnalyzer;
    private final CommandRegistry commandRegistry;

    private SkAnalyzerShell(String[] args) throws IOException {
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
                .addExtraPlugins(optionSet.valuesOf(addPluginSpec).toArray(File[]::new))
                .configProvider(new YamlConfigurateConfigProvider(Path.of("config.yml")))
                .build();

        this.commandRegistry = new CommandRegistry();

        this.skAnalyzer.start().whenComplete((_, ex) -> {
            if (ex != null) {
                this.skAnalyzer.getLogger().error("Something went wrong while trying to start SkAnalyzer", ex);
                System.exit(1);
                return;
            }

            this.commandRegistry.register(new ExitCommand(this));
            this.commandRegistry.register(new HelpCommand(this));
            this.commandRegistry.register(new LoadCommand(this));
            this.commandRegistry.register(new ParseCommand(this));
            this.commandRegistry.register(new UnloadCommand(this));

            this.skAnalyzer.getLogger().info("Type 'help' for help.");

            startReadingInput();
        });
    }

    static void main(String[] args) throws IOException {
        new SkAnalyzerShell(args);
    }

    public SkAnalyzer skAnalyzer() {
        return skAnalyzer;
    }

    public CommandRegistry commandRegistry() {
        return commandRegistry;
    }

    private void startReadingInput() {
        Thread thread = new Thread(
                new CommandInputHandler(this),
                "Command Input Thread"
        );

        thread.setDaemon(true);
        thread.start();
    }
}
