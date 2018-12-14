package eu.dzim.poc.fx.util;

import eu.dzim.poc.fx.FXApplication;

import java.net.URL;

public class Utils {

    public static URL getFXMLResource(Class<?> clazz) {
        return FXApplication.class.getResource(Constants.FXML_PREFIX + clazz.getSimpleName() + Constants.FXML_SUFFIX);
    }

    public static URL getFXMLResource(String name) {
        return FXApplication.class.getResource(Constants.FXML_PREFIX + name + Constants.FXML_SUFFIX);
    }
}
