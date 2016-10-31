package eu.dzim.shared.fx.integration.impl;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.dzim.shared.fx.integration.Plugin;
import eu.dzim.shared.fx.integration.PluginIntegrator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.util.Pair;

public class PluginServiceLoader implements PluginIntegrator {
	
	private static final Logger LOG = LogManager.getLogger(PluginServiceLoader.class.getName());
	
	private static final String JAR_PREFIX = "plugin.jar";
	
	private final ObservableMap<String, Plugin> plugins = FXCollections.observableMap(new TreeMap<>());
	
	public PluginServiceLoader() {
		// sonar
	}
	
	@Override
	public ObservableMap<String, Plugin> getPlugins() {
		return plugins;
	}
	
	private <T> Pair<ServiceLoader<T>, ClassLoader> loadIntegrations(Path path, Class<T> clazz) {
		if (path != null) {
			if (path.toFile().isFile() && path.getFileName().toString().endsWith(JAR_PREFIX)) {
				List<URL> fileNames = new ArrayList<>();
				try {
					fileNames.add(path.toUri().toURL());
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
				return loadJarIntegrations(fileNames, clazz);
			}
			List<URL> jarFileUrls = findJarFiles(path);
			if (jarFileUrls.isEmpty()) {
				return loadDirecotryIntegrations(path, clazz);
			} else {
				return loadJarIntegrations(jarFileUrls, clazz);
			}
		} else {
			return new Pair<>(ServiceLoader.load(clazz), getClass().getClassLoader());
		}
	}
	
	private <T> Pair<ServiceLoader<T>, ClassLoader> loadJarIntegrations(List<URL> fileNames, Class<T> clazz) {
		URL[] array = fileNames.stream().toArray(size -> new URL[size]);
		URLClassLoader cl = new URLClassLoader(array, getClass().getClassLoader());
		return new Pair<>(ServiceLoader.load(clazz, cl), cl);
	}
	
	private List<URL> findJarFiles(Path path) {
		List<URL> fileNames = new ArrayList<>();
		// FIXME use a glob instead of the filter below
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
			for (Path each : directoryStream) {
				if (!each.getFileName().toString().toLowerCase().endsWith(JAR_PREFIX))
					continue;
				fileNames.add(each.toUri().toURL());
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
		for (Path path : paths) {
			if (!Files.exists(path)) {
				LOG.warn("Path {} doesn't exist.", path.toString());
				continue;
			}
			Pair<ServiceLoader<Plugin>, ClassLoader> pluginServiceLoaderPair = loadIntegrations(path, Plugin.class);
			ServiceLoader<Plugin> pluginServiceLoader = pluginServiceLoaderPair.getKey();
			Iterator<Plugin> iterator = pluginServiceLoader.iterator();
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
		return plugins;
	}
}
