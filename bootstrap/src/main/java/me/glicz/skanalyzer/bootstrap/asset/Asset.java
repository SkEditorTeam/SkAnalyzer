package me.glicz.skanalyzer.bootstrap.asset;

import me.glicz.skanalyzer.bootstrap.Main;
import me.glicz.skanalyzer.bootstrap.util.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public record Asset(Type type, byte[] hash, String id, String path) {
    public static Asset[] read(Type type, BufferedReader reader) throws IOException {
        List<Asset> assets = new ArrayList<>();

        for (String line; (line = reader.readLine()) != null; ) {
            String[] parts = line.split("\t");
            assets.add(new Asset(type, Utils.fromHex(parts[0]), parts[1], parts[2]));
        }

        return assets.toArray(Asset[]::new);
    }

    public URL extractIfNeeded() throws IOException {
        Path path = Path.of(this.type.directory, this.path);
        URL url = path.toUri().toURL();

        if (Utils.verifyFile(path, this.hash)) {
            return url;
        }

        String resourcePath = path.toString().replace('\\', '/');

        try (InputStream is = Main.class.getResourceAsStream("/META-INF/" + resourcePath)) {
            if (is == null) {
                throw new IllegalStateException("Asset not found: " + resourcePath);
            }

            Files.deleteIfExists(path);
            Files.createDirectories(path.getParent());
            Files.copy(is, path);

            return url;
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
