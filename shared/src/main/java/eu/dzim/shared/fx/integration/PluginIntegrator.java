package eu.dzim.shared.fx.integration;

import javafx.collections.ObservableMap;

import java.nio.file.Path;

/**
 * Interface to abstract the loading of {@link Plugin}s, intended to be used by applications, such as the "root".
 *
 * @author bgmf
 */
public interface PluginIntegrator {

    /**
     * Load {@link Plugin}s from the provided {@link Path}s.<br>
     * The path can be
     * <ul>
     * <li>a specific JAR file to load</li>
     * <li>a directory with with JAR files</li>
     * <li>a directory with with class files (if there are no appropriate JAR files)</li>
     * </ul>
     * If all fails, the mechanism should check the local classloader.
     *
     * @param paths the paths to check
     * @return an {@link ObservableMap} (ID, {@link Plugin}) with the detected plugins
     */
    ObservableMap<String, Plugin> loadPlugins(Path... paths);

    /**
     * A simple getter for currently detected {@link Plugin}s.
     *
     * @return an {@link ObservableMap} (ID, {@link Plugin}) with the detected plugins
     * @see #loadPlugins(Path...)
     */
    ObservableMap<String, Plugin> getPlugins();
}
