package me.glicz.skanalyzer.plugin;

import io.papermc.paper.plugin.provider.classloader.ClassLoaderAccess;
import io.papermc.paper.plugin.provider.classloader.ConfiguredPluginClassLoader;
import io.papermc.paper.plugin.provider.classloader.PluginClassLoaderGroup;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("UnstableApiUsage")
final class GlobalClassLoaderGroup implements PluginClassLoaderGroup {
    private static final GlobalClassLoaderGroup INSTANCE = new GlobalClassLoaderGroup();

    private final List<ConfiguredPluginClassLoader> classLoaders = new CopyOnWriteArrayList<>();

    private GlobalClassLoaderGroup() {
    }

    static GlobalClassLoaderGroup get(ConfiguredPluginClassLoader classLoader) {
        if (!INSTANCE.classLoaders.contains(classLoader)) {
            INSTANCE.classLoaders.add(classLoader);
        }

        return INSTANCE;
    }

    @Override
    public @Nullable Class<?> getClassByName(String name, boolean resolve, ConfiguredPluginClassLoader requester) {
        try {
            return requester.loadClass(name, resolve, false, false);
        } catch (ClassNotFoundException ignored) {
        }

        for (ConfiguredPluginClassLoader classLoader : classLoaders) {
            try {
                return classLoader.loadClass(name, resolve, false, false);
            } catch (ClassNotFoundException ignored) {
            }
        }

        return null;
    }

    @Override
    public void remove(ConfiguredPluginClassLoader configuredPluginClassLoader) {
    }

    @Override
    public void add(ConfiguredPluginClassLoader configuredPluginClassLoader) {
    }

    @Override
    public ClassLoaderAccess getAccess() {
        return $ -> true;
    }
}
