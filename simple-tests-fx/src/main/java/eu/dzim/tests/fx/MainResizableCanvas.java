package eu.dzim.tests.fx;

import eu.dzim.tests.fx.controls.ResizableCanvas;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainResizableCanvas extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        ResizableCanvas canvas = new ResizableCanvas();

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(canvas);

        // Bind canvas size to stack pane size.
        // canvas.widthProperty().bind(
        // stackPane.widthProperty());
        // canvas.heightProperty().bind(
        // stackPane.heightProperty());
        canvas.bindTo(stackPane);

        primaryStage.setScene(new Scene(stackPane));
        primaryStage.setTitle("Tip 1: Resizable Canvas");
        primaryStage.show();
    }
}
