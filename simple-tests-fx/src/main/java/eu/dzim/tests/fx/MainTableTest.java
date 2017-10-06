package eu.dzim.tests.fx;

import java.io.IOException;

import eu.dzim.tests.fx.controller.FXMLDocumentController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainTableTest extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Koordinatensystem");
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainTableTest.class.getResource("/fxml/TableTest.fxml"));
			Pane rootLayout = (Pane) loader.load();
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}