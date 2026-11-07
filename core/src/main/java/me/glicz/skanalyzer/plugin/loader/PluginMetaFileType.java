package me.glicz.skanalyzer.plugin.loader;

import org.apache.commons.lang3.function.FailableFunction;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.InputStream;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

record PluginMetaFileType(
        String name,
        FailableFunction<? super InputStream, ? extends PluginDescriptionFile, ? extends Exception> metaFactory
) {
    public static final PluginMetaFileType PAPER = new PluginMetaFileType("paper-plugin.yml", UglyPaperPluginMetaParser::parse);
    public static final PluginMetaFileType SPIGOT = new PluginMetaFileType("plugin.yml", PluginDescriptionFile::new);

    private static final List<PluginMetaFileType> TYPES = List.of(PAPER, SPIGOT);

    public static PluginDescriptionFile resolve(JarFile jarFile) throws Exception {
        for (PluginMetaFileType type : TYPES) {
            JarEntry entry = jarFile.getJarEntry(type.name());

            if (entry == null) {
                continue;
            }

            try (InputStream is = jarFile.getInputStream(entry)) {
                return type.metaFactory().apply(is);
            }
        }

        throw new NullPointerException("No plugin meta file");
    }
}
