package eu.dzim.tests.fx;

import java.io.IOException;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainResize extends Application {
	
	private double xOffset = 0;
	private double yOffset = 0;
	
	@Override
	public void start(Stage stage) throws IOException {
		Label test = new Label();
		test.setMinSize(200, 200);
		test.setText("Drag Me To Move");
		test.setAlignment(Pos.CENTER);
		test.setStyle("-fx-background-color: red;");
		
		BorderPane pane = new BorderPane();
		pane.setStyle("-fx-background-color: beige");
		pane.setMinSize(400, 400);
		pane.setPrefSize(500, 500);
		pane.setCenter(test);
		
		// Listeners to move the undecorated Stage
		test.setOnMousePressed((MouseEvent e) -> {
			xOffset = e.getSceneX();
			yOffset = e.getSceneY();
		});
		test.setOnMouseDragged((MouseEvent e) -> {
			stage.setX(e.getScreenX() - xOffset);
			
			stage.setY(e.getScreenY() - yOffset);
		});
		
		Scene scene = new Scene(pane);
		
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setScene(scene);
		stage.show();
		
		// StyleHelper.resizeHelper(pane, stage);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public static class StyleHelper {
		
		public static final double MIN_WIDTH = 525.0;
		public static final double MIN_HEIGHT = 450.0;
		
		private static double dx;
		private static double dy;
		
		private static Boolean resizebottom = false;
		
		public static void resizeHelper(Node root, Stage stage) {
			Scene scene = stage.getScene();
			// Curser at "correct" position?
			// Change '10's to increse / decrease Action scope of Cursor
			root.setOnMousePressed((MouseEvent event) -> {
				if (event.getX() > stage.getWidth() - 10 && event.getX() < stage.getWidth() + 10 && event.getY() > stage.getHeight() - 10
						&& event.getY() < stage.getHeight() + 10) {
					resizebottom = true;
					dx = stage.getWidth() - event.getX();
					dy = stage.getHeight() - event.getY();
				} else {
					resizebottom = false;
				}
			});
			// Change Curser Style and Check which Border is hit
			root.setOnMouseMoved((MouseEvent event) -> {
				if (event.getSceneX() > scene.getWidth() - 10 && event.getSceneY() > scene.getHeight() - 10) {
					scene.setCursor(Cursor.SE_RESIZE);
				} else {
					scene.setCursor(Cursor.DEFAULT);
				}
			});
			// resize till min Size recognized
			root.setOnMouseDragged((MouseEvent event) -> {
				if (resizebottom == true) {
					double x = event.getX() + dx;
					double y = event.getY() + dy;
					if (x > MIN_WIDTH)
						stage.setWidth(event.getX() + dx);
					if (y > MIN_HEIGHT)
						stage.setHeight(event.getY() + dy);
				}
			});
		}
	}
}