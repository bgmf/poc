package eu.dzim.shared.fx.integration;

public class PluginException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public PluginException() {
		super();
	}
	
	public PluginException(String message) {
		super(message);
	}
	
	public PluginException(Throwable throwable) {
		super(throwable);
	}
	
	public PluginException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
