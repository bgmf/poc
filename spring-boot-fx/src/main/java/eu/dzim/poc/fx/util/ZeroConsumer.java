package eu.dzim.poc.fx.util;

/**
 * A "consumer" for a very specific type of functions: those, that need no input and will provide no result (hence, as "consumer"). <br>
 * We cannot call it a "supplier" either, since we have no output as well.
 * 
 * @author daniel.zimmermann@cnlab.ch
 */
@FunctionalInterface
public interface ZeroConsumer {
	/**
	 * Performs this operation.
	 */
	void accept();
}