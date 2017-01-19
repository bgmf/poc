package eu.dzim.tests.fx.math;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	
	@Override
	public void start(Stage stage) throws Exception {
		
		stage.setTitle("Math LaTex");
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/math/MathContainer.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root, 1200, 800);
		
		stage.setScene(scene);
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
