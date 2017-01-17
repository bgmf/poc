package eu.dzim.shared.util;

@FunctionalInterface
public interface SingleAcceptor<T> {
	void accept(T t);
}
