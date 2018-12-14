package eu.dzim.shared.fx.integration;

import eu.dzim.shared.disposable.Disposable;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Interface to describe a plugin for the a root application.
 *
 * @author bgmf
 */
public interface Plugin extends Disposable {

    /**
     * @return an {@link Optional} clone of the plugin instance. Needed to have multiple instances active (individual calculation, per project, ...)
     */
    Optional<Plugin> clonePlugin();

    /**
     * <b>This methods needs to be called prior to any operation like:</b>
     * <ul>
     * <li>{@link #name()}</li>
     * <li>{@link #version()}</li>
     * <li>{@link #description()}</li>
     * <li>{@link #open(Map)} / {@link #open(Path, Map)}</li>
     * </ul>
     * Because the values might need translation or rely on some other Guice dependecy injection.<br>
     * The method {@link #id()} should not be dependent on this.
     *
     * @param parentStage          the parent {@link Stage} object (e.g. from a root application, where the plugin is integrated into)
     * @param baseCssUrl           a {@link URL} to a common root CSSm if applicable
     * @param additionalParameters a {@link Map} of additional parameters for the plugin, if applicable
     * @throws PluginException when the data is incomplete (missing fields on {@link ProjectData}, ...) and the plugin can therefore not be initialized
     */
    void init(Stage parentStage, URL baseCssUrl, Map<String, Object> additionalParameters) throws PluginException;

    /**
     * Helper method to identify the state of the initialization.
     *
     * @return <code>true</code>, if this plugin instance is initialized
     */
    boolean isInitialized();

    /**
     * Method to fetch the ID of this plugin.
     *
     * @return the ID of this plugin as a string
     */
    String id();

    /**
     * Method to fetch the version string of this plugin.
     *
     * @return a version string (e.g. <code>major.minor.bugfix</code>)
     */
    String version();

    /**
     * Fetch a human readable name for this plugin to displayed somewhere.
     *
     * @return a human readable name
     */
    String name();

    /**
     * Fetch a human readable description for this plugin to displayed somewhere.
     *
     * @return a human readable description
     */
    String description();

    /**
     * The plugin should provide an {@link InputStream} with its icon. This icon can be used to display it in the container application.
     *
     * @return an {@link InputStream} of the plugins icon
     * @throws PluginException might be thrown, if the icon cannot be loaded
     */
    InputStream icon() throws PluginException;

    /**
     * Opens a new UI instance of this plugin.
     *
     * @param additionalParameters a {@link Map} of additional parameters for the plugin, if applicable
     * @throws PluginException might be thrown, if the UI cannot be loaded
     */
    void open(Map<String, Object> additionalParameters) throws PluginException;

    /**
     * Opens a new UI instance of this plugin.
     *
     * @param description          a specific file, encapsulated within a {@link Description} object, to load
     * @param additionalParameters a {@link Map} of additional parameters for the plugin, if applicable
     * @throws PluginException might be thrown, if the UI cannot be loaded
     */
    void open(Description description, Map<String, Object> additionalParameters) throws PluginException;

    /**
     * Helper method to figure out, whether or not the UI for this plugin is open.
     *
     * @return <code>true</code>, if the UI is open
     */
    boolean isOpen();

    /**
     * List the files, encapsulated within {@link Description} objects, belonging to this plugin under the specified path. The path might
     * be <code>null</code> to indicate an individual calculation (using a plugin's side default path).
     *
     * @param projectPath
     * @return
     */
    ObservableList<Description> listFiles();

    /**
     * get a {@link List} of extensions used by this plugin. The container application might use this to filter out valid files (from all plugins) and
     * to get a list of unrelated files (so called "additional files").
     *
     * @return a {@link List} of extensions (as strings) in use by this application
     */
    List<String> fileExtensions();
}
