package eu.dzim.shared.fx.util;

import javafx.application.Platform;

public class PlatformHelper {
	
	private PlatformHelper() {}
	
	public static void run(Runnable runnable) {
		if (runnable == null) {
			throw new IllegalArgumentException("The runnable to perform can not be null!");
		}
		if (Platform.isFxApplicationThread()) {
			runnable.run();
		} else {
			Platform.runLater(runnable);
		}
	}
}
