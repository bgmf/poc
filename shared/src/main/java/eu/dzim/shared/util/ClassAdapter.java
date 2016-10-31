package eu.dzim.shared.util;

import java.util.Optional;

/**
 * Helper interface inspired by org.eclipse.core.runtime.IAdaptable.<br>
 * <br>
 * 
 * <b>Quote:</b><br>
 * <i>An interface for an adaptable object.<br>
 * Adaptable objects can be dynamically extended to provide different interfaces (or "adapters"). Adapters are created by adapter factories, which are
 * in turn managed by type by adapter managers.<br>
 * <br>
 * For example,<br>
 * <code>IAdaptable a = [some adaptable];<br>
 * IFoo x = (IFoo)a.getAdapter(IFoo.class);<br>
 * if (x != null)<br>
 * [do IFoo things with x]<br>
 * <br>
 * <br>
 * This interface can be used without OSGi running.<br>
 * <br>
 * Clients may implement this interface, or obtain a default implementation of this interface by subclassing <code>PlatformObject</code>.</i>
 * 
 * @author bgmf
 */
public interface ClassAdapter {
	
	/**
	 * Returns an object which is an instance of the given class associated with this object.
	 * 
	 * @param adapter
	 *            the adapter class to look up
	 * @return an {@link Optional} of the given class - it may not be present (check with {@link Optional#isPresent()}), if this object does not have
	 *         an adapter for the given class
	 */
	<T> Optional<T> getAdapter(Class<T> adapter);
	
	/**
	 * Returns an object which is an instance of the given class name with this object.
	 * 
	 * @param adapter
	 *            the name of the class to load
	 * @return an {@link Optional} of the given class - it may not be present (check with {@link Optional#isPresent()}), if this object does not have
	 *         an adapter for the given class
	 */
	<T> Optional<T> getAdapter(String adapter);
}
