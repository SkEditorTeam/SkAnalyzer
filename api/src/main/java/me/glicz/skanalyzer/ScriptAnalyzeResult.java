package me.glicz.skanalyzer;

import me.glicz.skanalyzer.error.ScriptError;
import me.glicz.skanalyzer.structure.ScriptStructure;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;

public record ScriptAnalyzeResult(String jsonResult, List<ScriptError> errors, ScriptStructure structure) {
    @Override
    @Unmodifiable
    public List<ScriptError> errors() {
        return Collections.unmodifiableList(errors);
    }
}
