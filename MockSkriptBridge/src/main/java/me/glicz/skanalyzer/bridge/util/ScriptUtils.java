package me.glicz.skanalyzer.bridge.util;

import com.google.common.collect.ImmutableSet;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static ch.njol.skript.ScriptLoader.DISABLED_SCRIPT_PREFIX;
import static java.util.Objects.requireNonNullElseGet;

public final class ScriptUtils {
    public static final String SCRIPT_EXTENSION = ".sk";

    private ScriptUtils() {
    }

    public static boolean isValidFile(File file) {
        String fileName = file.getName();

        if (file.isDirectory()) {
            return !fileName.startsWith(".");
        }

        return !file.isHidden() && !fileName.startsWith(DISABLED_SCRIPT_PREFIX) && fileName.endsWith(SCRIPT_EXTENSION);
    }

    public static Set<File> listScripts(Iterable<File> files, boolean validateFiles) throws IOException {
        ImmutableSet.Builder<File> scripts = ImmutableSet.builder();

        for (File file : files) {
            collectScripts(file, scripts, validateFiles, false);
        }

        return scripts.build();
    }

    private static void collectScripts(File input, ImmutableSet.Builder<File> builder, boolean validateFiles, boolean validateDirs) throws IOException {
        File[] files = requireNonNullElseGet(input.listFiles(), () -> new File[]{input});

        for (File file : files) {
            if ((!file.isDirectory() || validateDirs) && validateFiles && !isValidFile(file)) {
                continue;
            }

            if (file.isDirectory()) {
                collectScripts(file, builder, true, true);
            } else if (file.isFile()) {
                builder.add(file.getCanonicalFile());
            }
        }
    }
}
