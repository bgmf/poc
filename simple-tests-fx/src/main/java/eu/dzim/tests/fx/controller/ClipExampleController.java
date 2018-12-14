package eu.dzim.tests.fx.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class ClipExampleController {

    @FXML private BorderPane root;
    @FXML private VBox vbox;

    @FXML
    protected void initialize() {
        Circle clip = new Circle();
        clip.radiusProperty().bind(vbox.widthProperty().divide(2).add(25));
        clip.centerXProperty().bind(root.widthProperty().divide(2));
        clip.centerYProperty().bind(root.heightProperty().divide(2));
        root.setClip(clip);
    }
}
