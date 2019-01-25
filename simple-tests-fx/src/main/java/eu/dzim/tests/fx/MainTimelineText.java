package eu.dzim.tests.fx;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainTimelineText extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    Stage primaryStage = null;

    @Override
    public void start(Stage stage) {

        this.primaryStage = stage;

        stage.setTitle("Window");
        stage.setMinWidth(640);
        stage.setMinHeight(480);

        final Label label = new Label();

        Button b1 = new Button("1) Show Text in 2.5s");
        b1.setOnAction(e -> {

            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(2500), ae -> {
                label.setText("Text after 2.5s");
                ae.consume();
            }));
            timeline.play();

            e.consume();
        });

        Button b2 = new Button("2) Show Text now and remove in 2.5s");
        b2.setOnAction(e -> {

            label.setText("Remove this text after 2.5s");
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(2500), ae -> {
                label.setText("");
                ae.consume();
            }));
            timeline.play();

            e.consume();
        });

        Button b3 = new Button("3) Show Text with multiple frames");
        b3.setOnAction(e -> {

            KeyFrame kf1 = new KeyFrame(Duration.ONE, ae -> label.setText("This ist the first text. Will be replaces in 2.5s"));
            KeyFrame kf2 = new KeyFrame(Duration.millis(2500), ae -> label.setText("This ist the second text. Will be remove in another 2.5s"));
            KeyFrame kf3 = new KeyFrame(Duration.millis(5000), ae -> label.setText("")); // be aware: all frames are started at the same time here

            Timeline timeline = new Timeline(kf1, kf2, kf3);
            timeline.play();

            e.consume();
        });

        Button b4 = new Button("4) Show Text with multiple frames, variation");
        b4.setOnAction(e -> {


            KeyFrame kf1 = new KeyFrame(Duration.ONE, new KeyValue(label.textProperty(), "This ist the first text. Will be replaces in 2.5s"));
            KeyFrame kf2 = new KeyFrame(Duration.millis(2500), new KeyValue(label.textProperty(), "This ist the second text. Will be remove in another 2.5s"));
            KeyFrame kf3 = new KeyFrame(Duration.millis(5000), new KeyValue(label.textProperty(), "")); // be aware: all frames are started at the same time here

            Timeline timeline = new Timeline(kf1, kf2, kf3);
            timeline.play();

            e.consume();
        });

        VBox container = new VBox(5.0);
        container.getChildren().addAll(b1, b2, b3, b4);

        BorderPane root = new BorderPane();
        root.setCenter(container);
        root.setBottom(label);

        Scene scene = new Scene(root, 640, 480);

        stage.setScene(scene);
        stage.show();
    }
}
