package me.glicz.skanalyzer.plugin;

import com.destroystokyo.paper.utils.PaperPluginLogger;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import io.papermc.paper.plugin.configuration.PluginMeta;
import io.papermc.paper.plugin.provider.classloader.ConfiguredPluginClassLoader;
import io.papermc.paper.plugin.provider.classloader.PluginClassLoaderGroup;
import me.glicz.skanalyzer.plugin.rewriter.PluginRewriter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.Nullable;

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
public class PluginClassLoader extends URLClassLoader implements ConfiguredPluginClassLoader {
    static {
        ClassLoader.registerAsParallelCapable();
    }

    private final PluginClassLoaderGroup group = GlobalClassLoaderGroup.get(this);
    private final Map<String, Class<?>> classes = new ConcurrentHashMap<>();
    private final PluginDescriptionFile configuration;
    private final File dataFolder, file;
    private final URL url;
    private final JarFile jarFile;
    private final @Nullable Manifest manifest;
    private @Nullable JavaPlugin plugin;

    public PluginClassLoader(ClassLoader parent, PluginDescriptionFile description, File dataFolder, File file, JarFile jarFile) throws Exception {
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
    public PluginMeta getConfiguration() {
        return configuration;
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve, boolean checkGlobal, boolean checkLibraries) throws ClassNotFoundException {
        return loadClass0(name, resolve, checkGlobal);
    }

    private Class<?> loadClass0(String name, boolean resolve, boolean checkGlobal) throws ClassNotFoundException {
        try {
            Class<?> result = super.loadClass(name, resolve);
            if (checkGlobal || result.getClassLoader() == this) {
                return result;
            }
        } catch (ClassNotFoundException ignored) {
        }

        if (checkGlobal) {
            Class<?> result = group.getClassByName(name, resolve, this);
            if (result != null) {
                return result;
            }
        }

        throw new ClassNotFoundException(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (name.startsWith("org.bukkit.") || name.startsWith("net.minecraft.")) {
            throw new ClassNotFoundException(name);
        }

        Class<?> result = classes.get(name);

        if (result != null) {
            return result;
        }

        String path = name.replace('.', '/').concat(".class");
        JarEntry entry = jarFile.getJarEntry(path);

        if (entry != null) {
            byte[] classBytes;

            try (InputStream is = jarFile.getInputStream(entry)) {
                classBytes = PluginRewriter.rewrite(ByteStreams.toByteArray(is));
            } catch (IOException ex) {
                throw new ClassNotFoundException(name, ex);
            }

            int dot = name.lastIndexOf('.');
            if (dot != -1) {
                String packageName = name.substring(0, dot);

                definePackage(packageName);
            }

            CodeSigner[] signers = entry.getCodeSigners();
            CodeSource source = new CodeSource(url, signers);

            result = defineClass(name, classBytes, 0, classBytes.length, source);
        }

        if (result == null) {
            result = super.findClass(name);
        }

        classes.put(name, result);

        return result;
    }

    private void definePackage(String packageName) {
        if (getDefinedPackage(packageName) != null) {
            return;
        }

        try {
            if (manifest != null) {
                definePackage(packageName, manifest, url);
            } else {
                definePackage(
                        packageName, null, null, null,
                        null, null, null, null
                );
            }
        } catch (IllegalArgumentException ex) {
            if (getDefinedPackage(packageName) != null) {
                return;
            }

            throw new IllegalStateException("Cannot find package " + packageName);
        }
    }

    @Override
    public void init(JavaPlugin plugin) {
        Preconditions.checkState(this.plugin == null);

        this.plugin = plugin;
        this.plugin.init(
                Bukkit.getServer(),
                configuration,
                dataFolder,
                file,
                this,
                configuration,
                PaperPluginLogger.getLogger(getConfiguration())
        );
    }

    @Override
    public @Nullable JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public PluginClassLoaderGroup getGroup() {
        return group;
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
