package eu.dzim.tests.fx;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainBlur extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        Button btn = new Button("If you can read this, the blur isn't working!");

        StackPane root = new StackPane();
        root.getChildren().add(btn);
        root.setStyle("-fx-background-color: black;");
        ColorAdjust adj = new ColorAdjust(0, -0.9, -0.5, 0);
        GaussianBlur blur = new GaussianBlur(55); // 55 is just to show edge effect more clearly.
        adj.setInput(blur);
        // root.setEffect(adj);

        Scene scene = new Scene(root, 500, 200, null);

        btn.setOnAction((ActionEvent event) -> {
            if (root.getEffect() == null)
                root.setEffect(adj);
            else
                scene.setFill(scene.getFill() == Color.BLACK ? null : Color.BLACK);
        });

        stage.setScene(scene);
        stage.show();
    }
}
