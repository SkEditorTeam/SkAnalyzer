package me.glicz.skanalyzer.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.logging.Level;

public class LevelSerializer implements JsonSerializer<Level> {
    @Override
    public JsonElement serialize(Level level, Type type, JsonSerializationContext ctx) {
        return new JsonPrimitive(level.getName());
    }
}