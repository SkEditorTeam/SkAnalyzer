package me.glicz.skanalyzer.util.serialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.glicz.skanalyzer.result.AnalyzeResult;
import me.glicz.skanalyzer.result.AnalyzeResults;

import java.io.File;
import java.util.logging.Level;

public final class Serialization {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(AnalyzeResult.class, AnalyzeResult.Serializer.INSTANCE)
            .registerTypeAdapter(AnalyzeResults.class, AnalyzeResults.Serializer.INSTANCE)
            .registerTypeAdapter(File.class, FileSerializer.INSTANCE)
            .registerTypeAdapter(Level.class, LevelSerializer.INSTANCE)
            .enableComplexMapKeySerialization()
            .disableHtmlEscaping()
            .create();

    private Serialization() {
    }
}
