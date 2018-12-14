package eu.dzim.tests.fx.utils;

import javafx.application.Platform;

/**
 * @author Thierry Wasylczenko
 */
public class PlatformHelper {

    private PlatformHelper() {
    }

    public static void run(Runnable treatment) {
        if (treatment == null) {
            throw new IllegalArgumentException("The treatment to perform can not be null");
        }
        if (Platform.isFxApplicationThread()) {
            treatment.run();
        } else {
            Platform.runLater(treatment);
        }
    }
}
