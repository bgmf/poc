package eu.dzim.shared.fx.integration;

public class JarLoaderException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public JarLoaderException() {
		super();
	}
	
	public JarLoaderException(String message) {
		super(message);
	}
	
	public JarLoaderException(Throwable throwable) {
		super(throwable);
	}
	
	public JarLoaderException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
