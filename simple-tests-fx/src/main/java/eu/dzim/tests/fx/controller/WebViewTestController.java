package eu.dzim.tests.fx.controller;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

public class WebViewTestController {

    @FXML private WebView webView;
    private WebEngine webEngine = null;

    @FXML
    protected void initialize() {
        webEngine = webView.getEngine();
        webEngine.setUserAgent("AppleWebKit/537.44");
        webEngine.load("https://appetize.io/docs");
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            JSObject window = (JSObject) webEngine.executeScript("window");
            JavaBridge bridge = new JavaBridge();
            window.setMember("java", bridge);
            webEngine.executeScript("console.log = function(message)\n" + "{\n" + "    java.log(message);\n" + "};");
        });
    }

    public class JavaBridge {
        public void log(String text) {
            System.out.println(text);
        }
    }
}
