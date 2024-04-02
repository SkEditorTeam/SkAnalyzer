package me.glicz.skanalyzer.error;

import com.google.gson.annotations.JsonAdapter;
import me.glicz.skanalyzer.util.LevelSerializer;

import java.util.logging.Level;

public record ScriptError(int line, String message, @JsonAdapter(LevelSerializer.class) Level level) {
}
