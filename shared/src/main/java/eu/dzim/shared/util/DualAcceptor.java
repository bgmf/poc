package eu.dzim.shared.util;

@FunctionalInterface
public interface DualAcceptor<T, U> {
    void accept(T t, U u);
}
