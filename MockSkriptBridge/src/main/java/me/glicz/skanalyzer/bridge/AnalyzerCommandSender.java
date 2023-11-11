package me.glicz.skanalyzer.bridge;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.glicz.skanalyzer.SkAnalyzer;
import me.glicz.skanalyzer.structure.StructureType;
import me.glicz.skanalyzer.structure.data.StructureData;
import org.bukkit.command.MessageCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnalyzerCommandSender implements MessageCommandSender {
    private final List<String> messages = new ArrayList<>();
    private final Gson gson = new Gson();

    @Override
    public void sendMessage(@NotNull String message) {
        messages.add(message);
    }

    public void finish(Map<String, Map<StructureType, List<StructureData>>> structures) {
        JsonObject jsonObject = new JsonObject();

        messages.forEach(message -> gson.fromJson(message, JsonObject.class).asMap().forEach((key, value) -> {
            JsonObject keyObject = getKeyObject(jsonObject, key);

            JsonArray errors = keyObject.getAsJsonArray("errors");
            if (errors == null) {
                errors = new JsonArray();
                keyObject.add("errors", errors);
            }
            errors.add(value);
        }));

        structures.forEach((key, value) -> {
            JsonObject keyObject = getKeyObject(jsonObject, key);

            value.forEach((structureType, structureValues) -> {
                JsonArray structuresArray = new JsonArray();
                structureValues.forEach(structureData -> structuresArray.add(gson.toJsonTree(structureData)));
                keyObject.add(structureType.name().toLowerCase() + "s", structuresArray);
            });
        });

        SkAnalyzer.get().getLogger().info(jsonObject);
    }

    private JsonObject getKeyObject(JsonObject jsonObject, String key) {
        JsonObject keyObject = jsonObject.getAsJsonObject(key);
        if (keyObject == null) {
            keyObject = new JsonObject();
            jsonObject.add(key, keyObject);
        }
        return keyObject;
    }
}
