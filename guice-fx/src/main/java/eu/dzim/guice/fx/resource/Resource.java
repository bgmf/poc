package eu.dzim.guice.fx.resource;

import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import eu.dzim.guice.fx.util.BaseEnumType;
import javafx.beans.Observable;
import javafx.beans.binding.Binding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectProperty;

/**
 * Interface to load string resources based on the currently selected locale.<br>
 * <br>
 * Intended to be used as a singleton instance via Guice.
 * 
 * @author daniel.zimmermann@cnlab.ch
 */
public interface Resource {
	
	/**
	 * Method to switch the language by the language string, for example &quot;en&quot;, &quot;de&quot;, ...
	 * 
	 * @param language
	 *            the language to set
	 * 
	 * @return <code>true</code>, if the language was changed (means: there is a bundle for the specified language and the current language is not the
	 *         new one)
	 */
	boolean setLanguage(String language);
	
	/**
	 * Method to switch the language by the language string, for example &quot;en&quot;, &quot;de&quot;, ...
	 * 
	 * @param locale
	 *            the {@link Locale} to set
	 * 
	 * @return <code>true</code>, if the language was changed (means: there is a bundle for the specified language and the current language is not the
	 *         new one)
	 */
	boolean setLocale(Locale locale);
	
	/**
	 * JavaFX {@link Property} bean for the current {@link Locale}.
	 * 
	 * @return the current {@link Locale} as a {@link ReadOnlyObjectProperty}
	 */
	ReadOnlyObjectProperty<Locale> localeProperty();
	
	/**
	 * Getter for the current {@link Locale}.
	 * 
	 * @return the current {@link Locale} object
	 */
	Locale getLocale();
	
	/**
	 * JavaFX {@link Property} bean for the current {@link ResourceBundle}.
	 * 
	 * @return the current {@link ResourceBundle} as a {@link ReadOnlyObjectProperty}
	 */
	ReadOnlyObjectProperty<ResourceBundle> resourceBundleProperty();
	
	/**
	 * Getter for the current {@link ResourceBundle}.
	 * 
	 * @return the current {@link ResourceBundle} object
	 */
	ResourceBundle getResourceBundle();
	
	/**
	 * Use this method, if you need only the plain string for a specific key.
	 * 
	 * @param key
	 *            the key for the resource
	 * 
	 * @return the string resource, or the key within exclamation marks, if the key is non-existent in the resources
	 */
	String getGuaranteedString(String key);
	
	/**
	 * Use this method, if the key you want to fetch contains formatting options (see {@link Formatter}).
	 * 
	 * @param key
	 *            the key for the resource
	 * @param args
	 *            the formatting arguments for the string resource
	 * 
	 * @return the formatted string resource, or something within exclamation marks, if the key is non-existent in the resources
	 */
	String getGuaranteedString(String key, Object... args);
	
	/**
	 * Use this method, if you need only the plain string for a specific key.
	 * 
	 * @param key
	 *            the key for the resource
	 * 
	 * @return an {@link Optional}, containing the string resource, if present
	 */
	Optional<String> getString(String key);
	
	/**
	 * Use this method, if the key you want to fetch contains formatting options (see {@link Formatter}).
	 * 
	 * @param key
	 *            the key for the resource
	 * @param args
	 *            the formatting arguments for the string resource
	 * 
	 * @return an {@link Optional}, containing the formatted string resource, if present
	 */
	Optional<String> getString(String key, Object... args);
	
	/**
	 * Use this method to fetch a {@link Boolean} from the resources.
	 * 
	 * @param key
	 *            the key for the resource
	 * 
	 * @return an {@link Optional}, containing the resource, if present and parsable
	 */
	Optional<Boolean> getBoolean(String key);
	
	/**
	 * Use this method to fetch a {@link Boolean} from the resources.
	 * 
	 * @param key
	 *            the key for the resource
	 * @param defaultValue
	 *            a default value, if the key is non-existent or otherwise invalid
	 * 
	 * @return either the {@link Boolean}, or the defined default value
	 */
	Boolean getBoolean(String key, Boolean defaultValue);
	
	/**
	 * Use this method to fetch a {@link Integer} from the resources.
	 * 
	 * @param key
	 *            the key for the resource
	 * 
	 * @return an {@link Optional}, containing the resource, if present and parsable
	 */
	Optional<Integer> getInteger(String key);
	
	/**
	 * Use this method to fetch a {@link Integer} from the resources.
	 * 
	 * @param key
	 *            the key for the resource
	 * @param defaultValue
	 *            a default value, if the key is non-existent or otherwise invalid
	 * @return either the {@link Integer}, or the defined default value
	 */
	Integer getInteger(String key, Integer defaultValue);
	
	/**
	 * Use this method to fetch a {@link Long} from the resources.
	 * 
	 * @param key
	 *            the key for the resource
	 * 
	 * @return an {@link Optional}, containing the resource, if present and parsable
	 */
	Optional<Long> getLong(String key);
	
	/**
	 * Use this method to fetch a {@link Long} from the resources.
	 * 
	 * @param key
	 *            the key for the resource
	 * @param defaultValue
	 *            a default value, if the key is non-existent or otherwise invalid
	 * @return either the {@link Long}, or the defined default value
	 */
	Long getLong(String key, Long defaultValue);
	
	/**
	 * Use this method to fetch a {@link Double} from the resources.
	 * 
	 * @param key
	 *            the key for the resource
	 * 
	 * @return an {@link Optional}, containing the resource, if present and parsable
	 */
	Optional<Double> getDouble(String key);
	
	/**
	 * Use this method to fetch a {@link Double} from the resources.
	 * 
	 * @param key
	 *            the key for the resource
	 * @param defaultValue
	 *            a default value, if the key is non-existent or otherwise invalid
	 * 
	 * @return either the {@link Double}, or the defined default value
	 */
	Double getDouble(String key, Double defaultValue);
	
	/**
	 * Supported Bindings: String
	 * 
	 * @param key
	 *            the key for the resource
	 * @param parameter
	 *            the formatting arguments for the string resource (results in a StringBinding only!)
	 * 
	 * @return a {@link StringBinding} to the resource
	 */
	Binding<String> getBinding(String key, Object... parameter);
	
	/**
	 * Supported Bindings: String, Boolean, Integer, Long, Double.<br>
	 * <br>
	 * <b>Note:</b> Parameterized bindings are <b>always</b> created as {@link StringBinding}s and are created on the fly and will not be stored as
	 * the unparameterized are.
	 * 
	 * @param key
	 *            the key for the resource
	 * @param defaultValue
	 *            a default value, if the key is non-existent or otherwise invalid
	 * @param parameter
	 *            the formatting arguments for the string resource (results in a StringBinding only!)
	 * 
	 * @return a parameterized (generic) {@link Binding} to the resource
	 */
	<T> Binding<T> getBinding(String key, T defaultValue, Object... parameter);
	
	/**
	 * Supported Bindings: String<br>
	 * <br>
	 * <b>Note:</b> {@link Binding}s created by this method are <b>always</b> created as {@link StringBinding}s and are created on the fly and will
	 * not be stored as the unparameterized are.<br>
	 * <br>
	 * <b>Note:</b> This is a convenient method for {@link #getBinding(ObjectProperty, String, List, Object...)}
	 * 
	 * @param type
	 *            the {@link ObjectProperty} of a {@link BaseEnumType} to get the key from and additionally listen to changes
	 * @param parameter
	 *            the formatting arguments for the string resource (results in a StringBinding only!)
	 * 
	 * @return a {@link StringBinding} to the resource specified by the current {@link BaseEnumType} stored in the {@link ObjectProperty} of the
	 *         <code>type</code> parameter
	 * 
	 * @see #getBinding(ObjectProperty, String, List, Object...)
	 */
	Binding<String> getBinding(final ObjectProperty<? extends BaseEnumType> type, final Object... parameter);
	
	/**
	 * Supported Bindings: String<br>
	 * <br>
	 * <b>Note:</b> {@link Binding}s created by this method are <b>always</b> created as {@link StringBinding}s and are created on the fly and will
	 * not be stored as the unparameterized are.<br>
	 * <br>
	 * <b>Note:</b> This is a convenient method for {@link #getBinding(ObjectProperty, String, List, Object...)}
	 * 
	 * @param type
	 *            the {@link ObjectProperty} of a {@link BaseEnumType} to get the key from and additionally listen to changes
	 * @param dependencies
	 *            additional dependencies to listen for changes
	 * @param parameter
	 *            the formatting arguments for the string resource (results in a StringBinding only!)
	 * 
	 * @return a {@link StringBinding} to the resource specified by the current {@link BaseEnumType} stored in the {@link ObjectProperty} of the
	 *         <code>type</code> parameter
	 */
	Binding<String> getBinding(final ObjectProperty<? extends BaseEnumType> type, final List<Observable> dependencies, final Object... parameter);
	
	/**
	 * Supported Bindings: String<br>
	 * <br>
	 * <b>Note:</b> {@link Binding}s created by this method are <b>always</b> created as {@link StringBinding}s and are created on the fly and will
	 * not be stored as the unparameterized are.
	 * 
	 * @param type
	 *            the {@link ObjectProperty} of a {@link BaseEnumType} to get the key from and additionally listen to changes
	 * @param dependencies
	 *            additional dependencies to listen for changes
	 * @param defaultValue
	 *            a default value, if the key is non-existent or otherwise invalid
	 * @param parameter
	 *            the formatting arguments for the string resource (results in a StringBinding only!)
	 * 
	 * @return a {@link StringBinding} to the resource specified by the current {@link BaseEnumType} stored in the {@link ObjectProperty} of the
	 *         <code>type</code> parameter
	 * 
	 * @see #getBinding(ObjectProperty, String, List, Object...)
	 */
	Binding<String> getBinding(final ObjectProperty<? extends BaseEnumType> type, final String defaultValue, final List<Observable> dependencies,
			final Object... parameter);
}
