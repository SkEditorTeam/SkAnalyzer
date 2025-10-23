package me.glicz.skanalyzer.shell.util.serialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.logging.Level;

public final class Serialization {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(File.class, FileSerializer.INSTANCE)
            .registerTypeAdapter(Level.class, LevelSerializer.INSTANCE)
            .enableComplexMapKeySerialization()
            .disableHtmlEscaping()
            .create();

    private Serialization() {
    }
}
