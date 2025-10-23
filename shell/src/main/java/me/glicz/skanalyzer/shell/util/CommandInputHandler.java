package me.glicz.skanalyzer.shell.util;

import me.glicz.skanalyzer.shell.SkAnalyzerShell;

import java.util.Arrays;
import java.util.Scanner;

public final class CommandInputHandler implements Runnable {
    private final Scanner scanner = new Scanner(System.in);
    private final SkAnalyzerShell app;

    public CommandInputHandler(SkAnalyzerShell app) {
        this.app = app;
    }

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
                app.commandRegistry().getCommand(args[0]).ifPresentOrElse(
                        command -> command.execute(Arrays.copyOfRange(args, 1, args.length)),
                        () -> app.skAnalyzer().getLogger().error("Unknown command: {}", args[0])
                );
            } catch (Exception e) {
                app.skAnalyzer().getLogger().atError()
                        .addArgument(Thread.currentThread())
                        .setCause(e)
                        .log("An exception occurred in {}. You should report this issue immediately.");
            }
        }
    }
}
