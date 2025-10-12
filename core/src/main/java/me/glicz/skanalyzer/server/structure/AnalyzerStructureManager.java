package me.glicz.skanalyzer.server.structure;

import org.bukkit.NamespacedKey;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class AnalyzerStructureManager implements StructureManager {
    @Override
    public Map<NamespacedKey, Structure> getStructures() {
        return Map.of();
    }

    @Override
    public @Nullable Structure getStructure(NamespacedKey structureKey) {
        return null;
    }

    @Override
    public @Nullable Structure registerStructure(NamespacedKey structureKey, Structure structure) {
        return null;
    }

    @Override
    public @Nullable Structure unregisterStructure(NamespacedKey structureKey) {
        return null;
    }

    @Override
    public @Nullable Structure loadStructure(NamespacedKey structureKey, boolean register) {
        return null;
    }

    @Override
    public @Nullable Structure loadStructure(NamespacedKey structureKey) {
        return null;
    }

    @Override
    public void saveStructure(NamespacedKey structureKey) {

    }

    @Override
    public void saveStructure(NamespacedKey structureKey, Structure structure) throws IOException {

    }

    @Override
    public void deleteStructure(NamespacedKey structureKey) throws IOException {

    }

    @Override
    public void deleteStructure(NamespacedKey structureKey, boolean unregister) throws IOException {

    }

    @Override
    public File getStructureFile(NamespacedKey structureKey) {
        return null;
    }

    @Override
    public Structure loadStructure(File file) throws IOException {
        return null;
    }

    @Override
    public Structure loadStructure(InputStream inputStream) throws IOException {
        return null;
    }

    @Override
    public void saveStructure(File file, Structure structure) throws IOException {

    }

    @Override
    public void saveStructure(OutputStream outputStream, Structure structure) throws IOException {

    }

    @Override
    public Structure createStructure() {
        return null;
    }

    @Override
    public Structure copy(Structure structure) {
        return null;
    }
}
