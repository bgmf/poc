package eu.dzim.shared.fx.integration.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.dzim.shared.fx.integration.JarLoader;
import eu.dzim.shared.fx.integration.JarLoaderException;
import eu.dzim.shared.util.SharedConstants;

public class JarLoaderImpl implements JarLoader {
	
	private static final Logger LOG = LogManager.getLogger(JarLoaderImpl.class.getName());
	
	public JarLoaderImpl() {
		// sonar
	}
	
	@Override
	public boolean executeJar(Path path, String... parameter) throws JarLoaderException {
		if (path == null) {
			throw new JarLoaderException("The path to the JAR file cannot be null!");
		}
		if (!Files.isRegularFile(path)) {
			LOG.warn("The file '{}' seems to be irregular.", path.toString());
			return false;
		}
		
		List<URL> fileNames = new ArrayList<>();
		try {
			fileNames.add(path.toUri().toURL());
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return false;
		}
		LOG.trace("JAR about to be loaded: {}", fileNames.get(0));
		URLClassLoader classLoader = loadJarIntegrations(fileNames);
		String mainClassString = getCustomManifestEntry(classLoader, SharedConstants.MANIFEST_ENTRY_MAIN_CLASS, null);
		if (mainClassString == null) {
			LOG.warn("The manifest of the JAR file '{}' did not provide the necessary {} entry.", path.toString(),
					SharedConstants.MANIFEST_ENTRY_MAIN_CLASS);
			return false;
		}
		LOG.debug("The manifest of the JAR file '{}' reports: {}={}", path.toString(), SharedConstants.MANIFEST_ENTRY_MAIN_CLASS, mainClassString);
		try {
			Class<?> mainClazz = classLoader.loadClass(mainClassString);
			Method mainMethod = mainClazz.getDeclaredMethod("main", String[].class);
			if (!mainMethod.isAccessible())
				mainMethod.setAccessible(true);
			if (parameter == null)
				parameter = new String[] {};
			mainMethod.invoke(null, (Object) parameter); // should have no result type
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new JarLoaderException("Could not load main class " + mainClassString, e);
		}
		return true;
	}
	
	private URLClassLoader loadJarIntegrations(List<URL> fileNames) {
		URL[] array = fileNames.stream().toArray(size -> new URL[size]);
		URLClassLoader cl = new URLClassLoader(array); // , getClass().getClassLoader()
		return cl;
	}
	
	public static String getCustomManifestEntry(URLClassLoader classLoader, String name, String defaultValue) {
		URL manifestUrl = classLoader.findResource(SharedConstants.MANIFEST);
		if (manifestUrl == null)
			return defaultValue;
		try {
			Manifest manifest = new Manifest(manifestUrl.openStream());
			Attributes attr = manifest.getMainAttributes();
			String value = attr.getValue(name);
			return value == null ? defaultValue : value;
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return defaultValue;
		}
	}
}
