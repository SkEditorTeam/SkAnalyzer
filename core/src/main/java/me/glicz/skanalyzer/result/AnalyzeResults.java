package me.glicz.skanalyzer.result;

import java.io.File;
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
}
