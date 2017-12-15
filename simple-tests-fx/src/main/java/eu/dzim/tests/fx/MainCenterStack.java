package eu.dzim.tests.fx;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainCenterStack extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		Label label = new Label("Hallo");
		// label.getTransforms().add(new Scale(15.50, 15.50));
		label.setScaleX(15.5);
		label.setScaleY(15.5);
		HBox hbox = new HBox();
		hbox.setStyle("-fx-background-color: orange;");
		hbox.getChildren().add(label);
		hbox.setAlignment(Pos.CENTER);
		StackPane root = new StackPane();
		root.getChildren().add(hbox);
		Scene scene = new Scene(root, 800, 600);
		stage.setScene(scene);
		stage.show();
	}
}
