package me.glicz.skanalyzer.result;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public record ScriptAnalyzeResults(Map<File, ScriptAnalyzeResult> results) {
    @Override
    public @Unmodifiable Map<File, ScriptAnalyzeResult> results() {
        return Map.copyOf(results);
    }

    public String jsonResult() {
        JsonObject jsonObject = new JsonObject();
        results.forEach((file, result) -> jsonObject.add(
                canonicalPath(file).replace('\\', '/'),
                result.toJsonObject()
        ));
        return jsonObject.toString();
    }

    private String canonicalPath(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
