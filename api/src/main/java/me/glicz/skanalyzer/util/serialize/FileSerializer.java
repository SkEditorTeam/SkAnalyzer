package me.glicz.skanalyzer.util.serialize;

import com.google.gson.*;

import java.io.File;
import java.lang.reflect.Type;

public class FileSerializer implements JsonSerializer<File>, JsonDeserializer<File> {
    public static final FileSerializer INSTANCE = new FileSerializer();

    private FileSerializer() {
    }

    @Override
    public JsonElement serialize(File value, Type type, JsonSerializationContext ctx) {
        return new JsonPrimitive(value.getAbsolutePath().replace("\\", "/"));
    }

    @Override
    public File deserialize(JsonElement value, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        return new File(value.getAsString());
    }
}
