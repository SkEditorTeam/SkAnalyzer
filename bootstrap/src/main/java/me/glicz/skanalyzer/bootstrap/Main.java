package me.glicz.skanalyzer.bootstrap;

import me.glicz.skanalyzer.bootstrap.asset.Asset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class Main {
    private static final String APP_MAIN = "me.glicz.skanalyzer.app.SkAnalyzerApp";

    public static void main(String[] args) throws MalformedURLException {
        Asset[] libraries = readAssets(Asset.Type.LIBRARY);

        URL[] urls = new URL[libraries.length];
        for (int i = 0; i < libraries.length; i++) {
            urls[i] = libraries[i].extractIfNeeded();
        }

        for (Asset plugin : readAssets(Asset.Type.PLUGIN)) {
            plugin.extractIfNeeded(); // just extract the plugin, not needed in classpath
        }

        ClassLoader classLoader = new URLClassLoader(urls, Main.class.getClassLoader());
        Thread thread = new Thread(() -> invokeAppMain(classLoader, args));
        thread.setContextClassLoader(classLoader);
        thread.start();
    }

    private static void invokeAppMain(ClassLoader classLoader, String[] args) {
        try {
            Class.forName(APP_MAIN, true, classLoader)
                    .getDeclaredMethod("main", String[].class)
                    .invoke(null, (Object) args);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static Asset[] readAssets(Asset.Type type) {
        InputStream is = Main.class.getResourceAsStream("/META-INF/" + type.directory() + ".list");
        if (is == null) return new Asset[0];

        try (is) {
            return Asset.read(type, new BufferedReader(new InputStreamReader(is)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
