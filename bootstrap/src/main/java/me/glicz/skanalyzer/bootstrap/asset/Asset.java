package me.glicz.skanalyzer.bootstrap.asset;

import me.glicz.skanalyzer.bootstrap.Main;
import me.glicz.skanalyzer.bootstrap.util.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public record Asset(Type type, byte[] hash, String id, String path) {
    public static Asset[] read(Type type, BufferedReader reader) throws IOException {
        List<Asset> assets = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\t");
            assets.add(new Asset(type, Utils.fromHex(parts[0]), parts[1], parts[2]));
        }

        return assets.toArray(Asset[]::new);
    }

    public URL extractIfNeeded() throws MalformedURLException {
        Path path = Path.of(type.directory, path());
        if (Utils.verifyFile(path, hash)) {
            return path.toUri().toURL();
        }

        InputStream is = Main.class.getResourceAsStream("/META-INF/" + path.toString().replace('\\', '/'));
        if (is == null) {
            return path.toUri().toURL();
        }

        try (is) {
            Files.deleteIfExists(path);
            Files.createDirectories(path.getParent());
            Files.copy(is, path);

            return path.toUri().toURL();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public enum Type {
        LIBRARY("libraries"),
        PLUGIN("plugins");

        private final String directory;

        Type(String directory) {
            this.directory = directory;
        }

        public String directory() {
            return directory;
        }
    }
}
