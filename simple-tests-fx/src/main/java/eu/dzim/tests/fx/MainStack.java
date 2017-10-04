package eu.dzim.tests.fx;

import eu.dzim.tests.fx.controls.StackedHBox;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainStack extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		
		stage.setTitle("Window");
		
		StackedHBox root = new StackedHBox();
		root.getEntries().add(new StackedHBox.Entry(5.0, Color.RED, "1"));
		root.getEntries().add(new StackedHBox.Entry(20.0, Color.GREEN, "2"));
		root.getEntries().add(new StackedHBox.Entry(75.0, Color.BLUE, "3"));
		
		Scene scene = new Scene(root, 640, 100);
		
		stage.setScene(scene);
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
