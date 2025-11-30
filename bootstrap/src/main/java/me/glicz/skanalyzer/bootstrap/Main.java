package me.glicz.skanalyzer.bootstrap;

import me.glicz.skanalyzer.bootstrap.asset.Asset;
import me.glicz.skanalyzer.bootstrap.util.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.net.URLClassLoader;

import static java.lang.invoke.MethodType.methodType;

public final class Main {
    private static final String MAIN_CLASS = "me.glicz.skanalyzer.shell.SkAnalyzerShell";

    public static void main(String[] args) throws IOException {
        Asset[] libraries = readAssets(Asset.Type.LIBRARY);

        URL[] urls = new URL[libraries.length];
        for (int i = 0; i < libraries.length; i++) {
            urls[i] = libraries[i].extractIfNeeded();
        }

        for (Asset plugin : readAssets(Asset.Type.PLUGIN)) {
            plugin.extractIfNeeded(); // just extract the plugin, not needed in classpath
        }

        ClassLoader classLoader = new URLClassLoader(urls, Main.class.getClassLoader());
        Thread thread = new Thread(() -> invokeMain(classLoader, args));
        thread.setContextClassLoader(classLoader);
        thread.start();
    }

    private static void invokeMain(ClassLoader classLoader, String[] args) {
        try {
            Class<?> mainClass = Class.forName(MAIN_CLASS, true, classLoader);

            MethodHandle mainHandle = MethodHandles.lookup()
                    .findStatic(mainClass, "main", methodType(void.class, String[].class))
                    .asFixedArity();

            mainHandle.invoke((Object) args);
        } catch (Throwable t) {
            throw Utils.sneakyThrow(t);
        }
    }

    private static Asset[] readAssets(Asset.Type type) throws IOException {
        InputStream is = Main.class.getResourceAsStream("/META-INF/" + type.directory() + ".list");
        if (is == null) {
            return new Asset[0];
        }

        try (is) {
            return Asset.read(type, new BufferedReader(new InputStreamReader(is)));
        }
    }
}
