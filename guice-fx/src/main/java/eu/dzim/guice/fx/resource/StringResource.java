package eu.dzim.guice.fx.resource;

import java.util.Formatter;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Helper class to load string resources based on the current active locale.
 * 
 * @author dzimmermann
 */
public class StringResource {
	
	/**
	 * The path, where the property file - containing translated strings - can be found. Be aware, that in the moment that means: inside the
	 * {@link ClassLoader}s hierarchy. Be also aware, that the {@link ResourceBundle} mechanism doesn't need neither the underscore-language part nor
	 * the dot-properties file ending to be attached.
	 */
	private static final String BUNDLE_NAME = String.format("%s.%s", StringResource.class.getPackage().getName(), "strings"); //$NON-NLS-1$
	
	/**
	 * A static reference to the current active language bundle. Per default the bundle for the English locale will be loaded.
	 */
	private static ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, Locale.ENGLISH);
	
	/**
	 * prevent instances
	 */
	private StringResource() {}
	
	/**
	 * Helper method to switch the language by the language string, for example &qout;en&qout;, &qout;de&qout;, ...
	 * 
	 * @param language
	 *          the language to set
	 * @return <code>true</code>, if the language was changed (means: there is a bundle for the specified language and the current language is not the
	 *         new one)
	 */
	public static synchronized boolean setLanguage(String language) {
		synchronized(RESOURCE_BUNDLE) {
			Locale current = Locale.getDefault();
			Locale next = new Locale(language.toLowerCase());
			if (current == null || !current.equals(next)) {
				RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, next);
				return true;
			}
			return false;
		}
	}
	
	/**
	 * getter for the current {@link ResourceBundle}, if we ever need it.
	 * 
	 * @return the current {@link ResourceBundle} object
	 */
	public static synchronized ResourceBundle getResourceBundle() {
		synchronized(RESOURCE_BUNDLE) {
			return RESOURCE_BUNDLE;
		}
	}
	
	/**
	 * Use this method, if you need only the plain string for a specific key.
	 * 
	 * @param key
	 *          the key for the string resource
	 * @return the string resource
	 */
	public static synchronized String getString(String key) {
		synchronized(RESOURCE_BUNDLE) {
			try {
				return RESOURCE_BUNDLE.getString(key);
			} catch (MissingResourceException e) {
				return '!' + key + '!';
			}
		}
	}
	
	/**
	 * Use this method, if the key you want to fetch contains formatting options (see {@link Formatter}).
	 * 
	 * @param key
	 *          the key for the string resource
	 * @param args
	 *          the formatting arguments for the string resource
	 * @return the formatted string resource
	 */
	public static synchronized String getString(String key, Object... args) {
		synchronized(RESOURCE_BUNDLE) {
			String value = "!empty!";
			try {
				value = RESOURCE_BUNDLE.getString(key);
			} catch (MissingResourceException e) {
				value = '!' + key + '!';
			}
			if (value.startsWith("!") && value.endsWith("!"))
				return value;
			return String.format(value, args);
		}
	}
}
