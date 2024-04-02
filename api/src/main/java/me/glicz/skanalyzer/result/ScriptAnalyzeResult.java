package me.glicz.skanalyzer.result;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.glicz.skanalyzer.error.ScriptError;
import me.glicz.skanalyzer.structure.ScriptStructure;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.util.List;

public record ScriptAnalyzeResult(File file, List<ScriptError> errors, ScriptStructure structure) {
    private static final Gson GSON = new Gson();

    @Override
    @Unmodifiable
    public List<ScriptError> errors() {
        return List.copyOf(errors);
    }

    public JsonObject toJsonObject() {
        JsonObject jsonObject = GSON.toJsonTree(structure).getAsJsonObject();
        jsonObject.add("errors", GSON.toJsonTree(errors));
        return jsonObject;
    }
}
