package eu.dzim.tests.fx;

import java.util.concurrent.atomic.AtomicInteger;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainBlink extends Application {
	
	@Override
	public void start(Stage primaryStage) throws InterruptedException {
		
		Label signalfeld = new Label();
		signalfeld.setPrefSize(100, 100);
		HBox box = new HBox(10);
		box.getChildren().add(signalfeld);
		
		primaryStage.setScene(new Scene(box));
		primaryStage.show();

		AtomicInteger counter = new AtomicInteger(0);
		final Timeline tl = new Timeline(new KeyFrame(Duration.millis(500), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				switch (counter.getAndIncrement()) {
				case 0:
					signalfeld.setStyle("-fx-background-color: grey;");
					break;
				case 1:
					signalfeld.setStyle("-fx-background-color: green;");
					break;
				case 2:
					signalfeld.setStyle("-fx-background-color: blue;");
					break;
				case 3:
					signalfeld.setStyle("-fx-background-color: red;");
					break;
				default:
					signalfeld.setStyle("-fx-background-color: black;");
					break;
				}
			}
		}));
		tl.cycleCountProperty().set(4);
		tl.play();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
