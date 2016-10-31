package eu.dzim.shared.fx.integration;

import java.nio.file.Path;

/**
 * Interface, whose implementations are intended to load a single JAR file into its own {@link ClassLoader}, and to execute a Main-Class (from the
 * JARs MANIFEST.MF).
 * 
 * @author bgmf
 *
 */
public interface JarLoader {
	
	boolean executeJar(Path path, String... parameter) throws JarLoaderException;
}
