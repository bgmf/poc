package eu.dzim.tests.fx;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Koord extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Koordinatensystem");
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Koord.class.getResource("/fxml/FXMLDocument.fxml"));
			Pane rootLayout = (Pane) loader.load();
			FXMLDocumentController controller = loader.getController();
			Scene scene = new Scene(rootLayout);
			controller.setSettings(scene);
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