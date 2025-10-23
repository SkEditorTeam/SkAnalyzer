package me.glicz.skanalyzer.result;

import me.glicz.skanalyzer.result.error.ScriptError;
import me.glicz.skanalyzer.result.structure.ScriptStructure;

import java.util.List;
import java.util.Set;

public record AnalyzeResult(
        List<ScriptError> errors,
        ScriptStructure structure,
        Set<String> usedAddons
) {
    public AnalyzeResult {
        errors = List.copyOf(errors);
        usedAddons = Set.copyOf(usedAddons);
    }
}
