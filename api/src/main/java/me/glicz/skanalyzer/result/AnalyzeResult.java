package me.glicz.skanalyzer.result;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import me.glicz.skanalyzer.result.structure.ScriptStructure;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

public record AnalyzeResult(
        List<ScriptError> errors,
        ScriptStructure structure,
        Set<String> usedAddons
) {
    public AnalyzeResult(
            List<ScriptError> errors,
            ScriptStructure structure,
            Set<String> usedAddons
    ) {
        this.errors = List.copyOf(errors);
        this.structure = structure;
        this.usedAddons = Set.copyOf(usedAddons);
    }

    public static class Serializer implements JsonSerializer<AnalyzeResult>, JsonDeserializer<AnalyzeResult> {
        public static final Serializer INSTANCE = new Serializer();

        private final static TypeToken<?> ERRORS_TYPE_TOKEN = TypeToken.getParameterized(List.class, ScriptError.class);
        private final static TypeToken<?> USED_ADDONS_TYPE_TOKEN = TypeToken.getParameterized(Set.class, String.class);

        private Serializer() {
        }

        @Override
        public JsonElement serialize(AnalyzeResult value, Type type, JsonSerializationContext ctx) {
            JsonObject object = ctx.serialize(value.structure).getAsJsonObject();

            object.add("errors", ctx.serialize(
                    value.errors, ERRORS_TYPE_TOKEN.getType()
            ));
            object.add("usedAddons", ctx.serialize(
                    value.usedAddons, USED_ADDONS_TYPE_TOKEN.getType()
            ));

            return object;
        }

        @Override
        public AnalyzeResult deserialize(JsonElement value, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            JsonObject object = value.getAsJsonObject();

            ScriptStructure structure = ctx.deserialize(object, ScriptStructure.class);
            List<ScriptError> errors = ctx.deserialize(object.get("errors"), ERRORS_TYPE_TOKEN.getType());
            Set<String> addons = ctx.deserialize(object.get("usedAddons"), USED_ADDONS_TYPE_TOKEN.getType());

            return new AnalyzeResult(errors, structure, addons);
        }
    }
}
