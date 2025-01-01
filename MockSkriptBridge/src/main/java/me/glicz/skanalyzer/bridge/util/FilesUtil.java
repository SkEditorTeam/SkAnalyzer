package me.glicz.skanalyzer.bridge.util;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class FilesUtil {
    public static Set<File> listScripts(File directory) {
        return !directory.isDirectory() ? Collections.singleton(directory) : listFiles(directory, ".sk");
    }

    private static Set<File> listFiles(File directory, String extension) {
        File[] files = directory.listFiles();
        if (files == null) {
            return Collections.emptySet();
        }

        Set<File> scripts = new HashSet<>();

        for (File file : files) {
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
