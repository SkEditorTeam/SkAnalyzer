package me.glicz.skanalyzer.util.serialize;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.logging.Level;

public class LevelSerializer implements JsonSerializer<Level>, JsonDeserializer<Level> {
    public static final LevelSerializer INSTANCE = new LevelSerializer();

    private LevelSerializer() {
    }

    @Override
    public JsonElement serialize(Level value, Type type, JsonSerializationContext ctx) {
        return new JsonPrimitive(value.getName());
    }

    @Override
    public Level deserialize(JsonElement value, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        return Level.parse(value.getAsString());
    }
}