package eu.dzim.shared.model.config;

import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public interface ConfigService {

    /**
     * Projects using this service (e.g. Orso 2018) should initiate their instance of this service by providing the path, where their config should be
     * stored at. Otherwise a default value will be used (<code>[user.home]/aschwanden</code>).
     *
     * @param configDir the {@link Path}, where the projects configuration should be stored at.
     */
    void init(Path configDir);

    /**
     * Triggers the operation to persist the current configuration.
     *
     * @return <code>true</code>, if the operation was successful
     */
    boolean update();

    /**
     * Checks the version of the file against the static value in the {@link CoreConstants}.
     *
     * @return <code>true</code>, if the file version matches
     */
    boolean checkFileVersion();

    /**
     * Retrieves the language from the last time, the project was used.
     *
     * @return the {@link Locale} of the used language {@link Stage}
     */
    Locale getLastLanguage();

    /**
     * Attempts to find the window data value stored for the given key.<br>
     * This operation should be used to store window data (JavaFX: {@link Stage}).
     *
     * @param key the key of the window data
     * @return an {@link Optional} containing the value of this key
     */
    <T> Optional<T> getAttribute(String key);

    /**
     * Attempts to set a generic window data value for the given key.<br>
     * This operation should be used to store window data (JavaFX: {@link Stage}).
     *
     * @param key   the key of the window data
     * @param value a generic window data value
     * @return <code>true</code>, if the operation was successful
     */
    <T> boolean setAttribute(String key, T value);

    /**
     * Attempts to set a generic window data value for the given key.<br>
     * This operation should be used to store window data (JavaFX: {@link Stage}).
     *
     * @param key    the key of the window data
     * @param value  a generic window data value
     * @param update <code>true</code>, if this operation should trigger the {@link #update()} operation (file system operation)
     * @return <code>true</code>, if the operation was successful
     */
    <T> boolean setAttribute(String key, T value, boolean update);

    /**
     * Retrieves a {@link List} of {@link RecentEntry} objects. Used for files recently used by this project.
     *
     * @return the {@link List} of {@link RecentEntry} objects
     */
    List<RecentEntry> getRecentEntries();

    /**
     * Add an {@link RecentEntry} object to the list.
     *
     * @param recentEntry the {@link RecentEntry} to add to the internal list
     * @return <code>true</code>, if the operation was successful
     */
    boolean addRecentEntry(RecentEntry recentEntry);

    /**
     * Add the file {@code filename} with the given project name {@code projectName} to the recents list.
     *
     * @param projectName name of the project
     * @param filename    full path
     * @return <code>true</code>, if the operation was successful
     */
    boolean addRecentEntry(String projectName, Path filename);
}
