package eu.dzim.tests.fx;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class MainPopup extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		
		stage.setTitle("Window");
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TableTest2.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root, 1200, 800);
		
		try {
			Popup popup = new Popup();
			FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/TableTest2.fxml"));
			Pane pane = loader2.load();
			popup.getContent().add(pane);
			popup.show(root, 0.0, 0.0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// scene.setFill(Color.TRANSPARENT);
		// stage.initStyle(StageStyle.TRANSPARENT);
		
		// Stage two = new Stage();
		// two.setTitle("two");
		// FXMLLoader loader2 = new FXMLLoader(getClass().getResource("TableTest2.fxml"));
		// Parent root2 = loader2.load();
		// Scene scene2 = new Scene(root2, 1200, 800);
		// two.setScene(scene2);
		// two.show();
		
		stage.setScene(scene);
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
