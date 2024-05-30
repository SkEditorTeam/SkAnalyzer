package me.glicz.skanalyzer.bridge.util;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class FilesUtil {
    public static Set<File> listScripts(File directory) {
        if (!directory.isDirectory()) {
            return Set.of(directory);
        }
        return listFiles(directory, ".sk");
    }

    private static Set<File> listFiles(File directory, String extension) {
        Set<File> scripts = new HashSet<>();

        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                scripts.addAll(listFiles(file, extension));
                continue;
            }

            if (file.getName().endsWith(extension)) {
                scripts.add(file);
            }
        }

        return scripts;
    }
}
