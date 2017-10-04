package eu.dzim.tests.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainExample extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("Application Window");
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Root.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root, 640, 480);
		stage.setScene(scene);
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
