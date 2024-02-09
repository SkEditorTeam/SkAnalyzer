package me.glicz.skanalyzer.bridge;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import me.glicz.skanalyzer.ScriptAnalyzeResult;
import me.glicz.skanalyzer.SkAnalyzer;
import me.glicz.skanalyzer.error.ScriptError;
import me.glicz.skanalyzer.structure.ScriptStructure;
import me.glicz.skanalyzer.structure.data.StructureData;
import org.bukkit.command.MessageCommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@RequiredArgsConstructor
public class AnalyzerCommandSender implements MessageCommandSender {
    private final List<String> messages = new ArrayList<>();
    private final Gson gson = new Gson();
    private final SkAnalyzer skAnalyzer;

    @Override
    public void sendMessage(@NotNull String message) {
        messages.add(message);
    }

    public ScriptAnalyzeResult finish(File file, ScriptStructure structure) {
        JsonObject jsonObject = new JsonObject();
        JsonObject fileObject = new JsonObject();

        List<ScriptError> scriptErrors = new ArrayList<>();

        messages.forEach(message -> {
            JsonObject error = gson.fromJson(message, JsonObject.class);

            JsonArray errors = fileObject.getAsJsonArray("errors");
            if (errors == null) {
                errors = new JsonArray();
                fileObject.add("errors", errors);
            }
            errors.add(error);

            scriptErrors.add(new ScriptError(
                    error.get("line").getAsInt(),
                    error.get("message").getAsString(),
                    Level.parse(error.get("level").getAsString())
            ));
        });

        parseStructureDataList(fileObject, "commands", structure.commandDataList());
        parseStructureDataList(fileObject, "events", structure.eventDataList());
        parseStructureDataList(fileObject, "functions", structure.functionDataList());
        fileObject.add("options", gson.toJsonTree(structure.options()));

        jsonObject.add(getCanonicalPath(file).replace('\\', '/'), fileObject);

        skAnalyzer.getLogger().info(jsonObject);

        return new ScriptAnalyzeResult(jsonObject.toString(), scriptErrors, structure);
    }

    private String getCanonicalPath(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void parseStructureDataList(JsonObject jsonObject, String type, List<? extends StructureData> structureDataList) {
        JsonArray structuresArray = new JsonArray();
        structureDataList.forEach(structureData -> structuresArray.add(gson.toJsonTree(structureData)));
        jsonObject.add(type, structuresArray);
    }
}
