package eu.dzim.tests.fx.controller;

import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class FillChangerController {
	
	@FXML private BorderPane root;
	@FXML private VBox center;
	
	@FXML private Rectangle rect;
	
	@FXML
	protected void initialize() {
		rect.widthProperty().bind(center.widthProperty());
		rect.heightProperty().bind(center.heightProperty());
		
		FillTransition fillTransition = new FillTransition();
		
		ObjectProperty<Color> baseColor = new SimpleObjectProperty<>();
		
		KeyValue keyValue1 = new KeyValue(baseColor, Color.RED);
		KeyValue keyValue2 = new KeyValue(baseColor, Color.YELLOW);
		KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValue1);
		KeyFrame keyFrame2 = new KeyFrame(Duration.millis(500), keyValue2);
		// Timeline timeline = new Timeline(keyFrame1, keyFrame2);
		
		baseColor.addListener((obs, oldColor, newColor) -> {
			
			rect.setFill(Paint.valueOf(String.format("-gradient-base: #%02x%02x%02x; ", (int) (newColor.getRed() * 255),
					(int) (newColor.getGreen() * 255), (int) (newColor.getBlue() * 255))));
		});
		
		// timeline.setAutoReverse(true);
		// timeline.setCycleCount(Animation.INDEFINITE);
		// timeline.play();
	}
}
