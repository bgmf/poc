package eu.dzim.shared.fx.integration.impl;

import eu.dzim.shared.fx.integration.Plugin;
import eu.dzim.shared.fx.integration.PluginIntegrator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class PluginServiceLoader implements PluginIntegrator {

    private static final Logger LOG = LogManager.getLogger(PluginServiceLoader.class.getName());

    private static final String JAR_FILE = "plugin.jar";

    private final ObservableMap<String, Plugin> plugins = FXCollections.observableMap(new TreeMap<>());

    public PluginServiceLoader() {
        // sonar
    }

    @Override
    public ObservableMap<String, Plugin> getPlugins() {
        return plugins;
    }

    private <T> List<Pair<ServiceLoader<T>, ClassLoader>> loadIntegrations(Path path, Class<T> clazz) {
        if (path != null) {
            if (path.toFile().isFile() && path.getFileName().toString().endsWith(JAR_FILE)) {
                return Collections.singletonList(loadJarIntegrations(Collections.singletonList(path), clazz));
            }
            List<Path> jarFiles = findJarFiles(path);
            if (jarFiles.isEmpty()) {
                return Collections.singletonList(loadDirecotryIntegrations(path, clazz));
            } else {
                return jarFiles.stream().map(jarFile -> loadDirecotryIntegrations(jarFile, clazz)).collect(Collectors.toList());
            }
        } else {
            return Collections.singletonList(new Pair<>(ServiceLoader.load(clazz), getClass().getClassLoader()));
        }
    }

    private <T> Pair<ServiceLoader<T>, ClassLoader> loadJarIntegrations(List<Path> files, Class<T> clazz) {
        // TODO add sub-directory of the same name and all its containing jars
        URL[] array = files.stream().map(path -> {
            try {
                return path.toUri().toURL();
            } catch (MalformedURLException e) {
                LOG.error(e);
                return null;
            }
        }).filter(url -> url != null).toArray(size -> new URL[size]);
        URLClassLoader cl = new URLClassLoader(array, getClass().getClassLoader());
        return new Pair<>(ServiceLoader.load(clazz, cl), cl);
    }

    private List<Path> findJarFiles(Path path) {
        List<Path> fileNames = new ArrayList<>();
        // FIXME use a glob instead of the filter below
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
            for (Path each : directoryStream) {
                if (!each.getFileName().toString().toLowerCase().endsWith(JAR_FILE))
                    continue;
                fileNames.add(each);
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return fileNames;
    }

    private <T> Pair<ServiceLoader<T>, ClassLoader> loadDirecotryIntegrations(Path path, Class<T> clazz) {
        List<URL> fileNames = new ArrayList<>();
        try {
            fileNames.add(path.toUri().toURL());
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        URL[] array = fileNames.stream().toArray(size -> new URL[size]);
        URLClassLoader cl = new URLClassLoader(array, getClass().getClassLoader());
        return new Pair<>(ServiceLoader.load(clazz, cl), cl);
    }

    @Override
    public ObservableMap<String, Plugin> loadPlugins(Path... paths) {

        ServiceLoader<Plugin> internal = ServiceLoader.load(Plugin.class);
        Iterator<Plugin> internalIt = internal.iterator();
        while (internalIt.hasNext()) {
            Plugin plugin = internalIt.next();
            if (plugins.get(plugin.id()) != null) {
                // && CoreUtils.versionCompare(plugins.get(plugin.id()).version(), plugin.version()) <= 0) {
                continue;
            }
            plugins.put(plugin.id(), plugin);
            LOG.trace("Found and added internal plugin: {}", plugin.id());
        }

        if (!plugins.isEmpty())
            return plugins;

        for (Path path : paths) {
            if (!Files.exists(path)) {
                LOG.warn("Path {} doesn't exist.", path.toString());
                continue;
            }
            List<Pair<ServiceLoader<Plugin>, ClassLoader>> integrations = loadIntegrations(path, Plugin.class);
            for (Pair<ServiceLoader<Plugin>, ClassLoader> integration : integrations) {
                ServiceLoader<Plugin> serviceLoader = integration.getKey();
                Iterator<Plugin> iterator = serviceLoader.iterator();
                while (iterator.hasNext()) {
                    Plugin plugin = iterator.next();
                    if (plugins.get(plugin.id()) != null) {
                        // && CoreUtils.versionCompare(plugins.get(plugin.id()).version(), plugin.version()) <= 0) {
                        continue;
                    }
                    plugins.put(plugin.id(), plugin);
                    LOG.trace("Found and added plugin: {}", plugin.id());
                }
            }
        }
        return plugins;
    }
}
