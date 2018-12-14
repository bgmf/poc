package eu.dzim.tests.fx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class MultiInstanceTestRootController {

    @FXML private BorderPane rootPane;
    @FXML private SplitPane contentPane;

    @FXML
    protected void initialize() {
        for (int i = 0; i < 5; i++) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("./MultiInstanceTestPane.fxml"));
                Pane pane = loader.load();
                ((MultiInstanceTestPaneController) loader.getController()).updateIDAndHeader("[" + (i + 1) + "]", "Pane " + (i + 1));
                contentPane.getItems().add(pane);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
