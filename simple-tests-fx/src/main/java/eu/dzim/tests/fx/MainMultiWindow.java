package eu.dzim.tests.fx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainMultiWindow extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    Stage primaryStage = null;

    @Override
    public void start(Stage stage) {

        this.primaryStage = stage;

        stage.setTitle("Main Window");
        stage.setMinWidth(640);
        stage.setMinHeight(480);

        Button open = new Button("Show Secondary Window");
        open.setOnAction(e -> {
            showSecondaryStage();
            e.consume();
        });

        BorderPane root = new BorderPane();
        root.setCenter(open);

        Scene scene = new Scene(root, 640, 480);

        stage.setScene(scene);
        stage.show();
    }

    private void showSecondaryStage() {

        Stage stage = new Stage(StageStyle.DECORATED);
        // WINDOW_MODAL = only modal to its owner window(s)
        // APPLICATION_MODAL = modal to every window - Dialogs and alike
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(this.primaryStage);
        stage.setTitle("Secondary Window");
        stage.setMinWidth(640);
        stage.setMinHeight(480);

        BorderPane root = new BorderPane();
        root.setCenter(new Label("Secondary Window Content"));

        Scene scene = new Scene(root, 640, 480);

        stage.setScene(scene);
        stage.show();
    }
}
