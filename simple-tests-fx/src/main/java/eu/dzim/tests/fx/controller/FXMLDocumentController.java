package eu.dzim.tests.fx.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

public class FXMLDocumentController {
	
	@FXML private Pane drawPane;
	
	@FXML private ResourceBundle resources;
	@FXML private URL location;
	
	@FXML
	protected void initialize() {
		// do something here, if necessary
	}
	
	public void setSettings(Scene scene) {
		
		// Erster Versuch
		Line line = new Line();
		line.setStartX(200);
		line.setEndX(200);
		line.setStartY(0);
		line.setEndY(400);
		line.setStroke(Paint.valueOf("yellow"));
		
		// Zweiter Versuch
		Line line2 = new Line();
		line2.setStartX(drawPane.getWidth() / 2);
		line2.setStartY(0);
		line2.setEndX(drawPane.getWidth() / 2);
		line2.setEndY(drawPane.getHeight());
		line2.setStroke(Paint.valueOf("green"));
		
		// Dritter Versuch
		Line line3 = new Line();
		line3.setStroke(Paint.valueOf("red"));
		
		line3.startXProperty().bind(drawPane.widthProperty().divide(2));
		line3.endXProperty().bind(drawPane.widthProperty().divide(2));
		
		line3.startYProperty().bind(drawPane.heightProperty());
		line3.endYProperty().bind(drawPane.heightProperty());
		
		Line line4 = new Line();
		
		drawPane.getChildren().add(line);
		drawPane.getChildren().add(line2);
		drawPane.getChildren().add(line3);
		drawPane.getChildren().add(line4);
		
		line4.setStroke(Paint.valueOf("blue"));
		scene.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
			System.out.println("Width: " + newSceneWidth);
			line4.setStartX(newSceneWidth.doubleValue() / 2);
			line4.setEndX(newSceneWidth.doubleValue() / 2);
			
		});
		scene.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> {
			System.out.println("Height: " + newSceneHeight);
			line4.setStartY(0);
			line4.setEndY(newSceneHeight.doubleValue());
		});
	}
}