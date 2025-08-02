package me.glicz.skanalyzer.bridge.util;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static ch.njol.skript.ScriptLoader.DISABLED_SCRIPT_PREFIX;
import static java.util.Objects.requireNonNullElseGet;

public final class ScriptUtils {
    public static final String SCRIPT_EXTENSION = ".sk";

    private ScriptUtils() {
    }

    public static Set<File> listScripts(File input) throws IOException {
        File[] files = requireNonNullElseGet(input.listFiles(), () -> new File[]{input});

        Set<File> scripts = new HashSet<>();

        for (File file : files) {
            if (file.isDirectory()) {
                scripts.addAll(listScripts(file));
                continue;
            }

            String fileName = file.getName();
            if (!fileName.startsWith(DISABLED_SCRIPT_PREFIX) && fileName.endsWith(SCRIPT_EXTENSION)) {
                scripts.add(file.getCanonicalFile());
            }
        }

        return scripts;
    }
}
