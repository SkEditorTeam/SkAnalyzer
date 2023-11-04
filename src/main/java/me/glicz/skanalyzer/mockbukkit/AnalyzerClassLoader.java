package me.glicz.skanalyzer.mockbukkit;

import be.seeseemelk.mockbukkit.plugin.MockBukkitPluginClassLoaderGroup;
import com.destroystokyo.paper.utils.PaperPluginLogger;
import com.google.common.io.ByteStreams;
import io.papermc.paper.plugin.configuration.PluginMeta;
import io.papermc.paper.plugin.provider.classloader.ConfiguredPluginClassLoader;
import io.papermc.paper.plugin.provider.classloader.PluginClassLoaderGroup;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

@SuppressWarnings("UnstableApiUsage")
public class AnalyzerClassLoader extends URLClassLoader implements ConfiguredPluginClassLoader {
    static {
        ClassLoader.registerAsParallelCapable();
    }

    @Getter(onMethod_={@NotNull})
    private final PluginClassLoaderGroup group = new MockBukkitPluginClassLoaderGroup();
    private final Map<String, Class<?>> classes = new ConcurrentHashMap<>();
    @Getter
    private final PluginDescriptionFile configuration;
    private final File dataFolder, file;
    private final URL url;
    private final JarFile jarFile;
    private final Manifest manifest;

    public AnalyzerClassLoader(ClassLoader parent, PluginDescriptionFile description, File dataFolder, File file, JarFile jarFile) throws Exception {
        super(file.getName(), new URL[]{file.toURI().toURL()}, parent);
        this.configuration = description;
        this.dataFolder = dataFolder;
        this.file = file;
        this.url = file.toURI().toURL();
        this.jarFile = jarFile;
        this.manifest = jarFile.getManifest();
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return loadClass0(name, resolve, true);
    }

    @Override
    public Class<?> loadClass(@NotNull String name, boolean resolve, boolean checkGlobal, boolean checkLibraries) throws ClassNotFoundException {
        return loadClass0(name, resolve, checkGlobal);
    }

    private Class<?> loadClass0(String name, boolean resolve, boolean checkGlobal) throws ClassNotFoundException {
        try {
            Class<?> result = super.loadClass(name, resolve);
            if (checkGlobal || result.getClassLoader() == this)
                return result;
        } catch (ClassNotFoundException ignored) {
        }

        if (checkGlobal) {
            Class<?> result = group.getClassByName(name, resolve, this);
            if (result != null)
                return result;
        }

        throw new ClassNotFoundException(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (name.startsWith("org.bukkit.") || name.startsWith("net.minecraft.")) {
            throw new ClassNotFoundException(name);
        }
        Class<?> result = classes.get(name);

        if (result == null) {
            String path = name.replace('.', '/').concat(".class");
            JarEntry entry = jarFile.getJarEntry(path);

            if (entry != null) {
                byte[] classBytes;

                try (InputStream is = jarFile.getInputStream(entry)) {
                    classBytes = ByteStreams.toByteArray(is);
                } catch (IOException ex) {
                    throw new ClassNotFoundException(name, ex);
                }

                int dot = name.lastIndexOf('.');
                if (dot != -1) {
                    String pkgName = name.substring(0, dot);
                    if (getDefinedPackage(pkgName) == null) {
                        try {
                            if (manifest != null)
                                definePackage(pkgName, manifest, url);
                            else definePackage(
                                    pkgName, null, null, null,
                                    null, null, null, null
                            );
                        } catch (IllegalArgumentException ex) {
                            if (getDefinedPackage(pkgName) == null)
                                throw new IllegalStateException("Cannot find package " + pkgName);
                        }
                    }
                }

                CodeSigner[] signers = entry.getCodeSigners();
                CodeSource source = new CodeSource(url, signers);

                result = defineClass(name, classBytes, 0, classBytes.length, source);
            }

            if (result == null)
                result = super.findClass(name);

            classes.put(name, result);
        }

        return result;
    }

    @Override
    public void init(JavaPlugin plugin) {
        plugin.init(
                Bukkit.getServer(),
                configuration,
                dataFolder,
                file,
                this,
                configuration,
                PaperPluginLogger.getLogger((PluginMeta) getConfiguration())
        );
    }

    @Override
    public @Nullable JavaPlugin getPlugin() {
        return null;
    }

    @Override
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            jarFile.close();
        }
    }
}
