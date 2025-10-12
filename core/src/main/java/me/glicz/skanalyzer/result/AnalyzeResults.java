package me.glicz.skanalyzer.result;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import me.glicz.skanalyzer.util.serialize.Serialization;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

public final class AnalyzeResults {
    private final Map<File, AnalyzeResult> results;

    public AnalyzeResults(Map<File, AnalyzeResult> results) {
        this.results = Map.copyOf(results);
    }

    public Set<File> files() {
        return results.keySet();
    }

    public AnalyzeResult result(File file) {
        return results.get(file);
    }

    @Override
    public String toString() {
        return Serialization.GSON.toJson(this);
    }

    public static class Serializer implements JsonSerializer<AnalyzeResults>, JsonDeserializer<AnalyzeResults> {
        public static final Serializer INSTANCE = new Serializer();
        private static final TypeToken<?> TYPE_TOKEN = TypeToken.getParameterized(Map.class, File.class, AnalyzeResult.class);

        private Serializer() {
        }

        @Override
        public JsonElement serialize(AnalyzeResults value, Type type, JsonSerializationContext ctx) {
            return ctx.serialize(value.results, TYPE_TOKEN.getType());
        }

        @Override
        public AnalyzeResults deserialize(JsonElement value, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            return new AnalyzeResults(ctx.deserialize(value, TYPE_TOKEN.getType()));
        }
    }
}
