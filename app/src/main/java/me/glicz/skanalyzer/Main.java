package me.glicz.skanalyzer;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.printf("SkAnalyzer v%s - simple Skript parser. Created by Glicz.%n", Main.class.getPackage().getSpecificationVersion());
        SkAnalyzer skAnalyzer = SkAnalyzer.builder()
                .flags(parseFlags(args))
                .build();
        startReadingInput(skAnalyzer);
    }

    private static AnalyzerFlag[] parseFlags(String[] args) {
        return Arrays.stream(args)
                .map(AnalyzerFlag::getByArg)
                .filter(Objects::nonNull)
                .toArray(AnalyzerFlag[]::new);
    }

    private static void startReadingInput(SkAnalyzer skAnalyzer) {
        Thread thread = new Thread() {
            private final Scanner scanner = new Scanner(System.in);

            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    if (scanner.hasNext()) {
                        String line = scanner.nextLine();
                        if (line != null) {
                            if (line.trim().equals("exit")) {
                                System.exit(0);
                            }
                            skAnalyzer.parseScript(line);
                        }
                    }
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }
}
