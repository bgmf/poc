package eu.dzim.tests.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainWithControllerFactory extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		
		FXMLLoader loader = new FXMLLoader();
		loader.setControllerFactory(this::controllerForClass);
		loader.setLocation(getClass().getResource("/fxml/TableTest2.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root, 1200, 800);
		
		// scene.setFill(Color.TRANSPARENT);
		// stage.initStyle(StageStyle.TRANSPARENT);
		
		stage.setScene(scene);
		stage.show();
	}
	
	private Object controllerForClass(Class<?> clazz) {
		try {
			Object controllerInstance = clazz.newInstance();
			// controllerInstance#someArbitraryMethodCall if necessary
			// will be triggered *before* the initialize method!
			return controllerInstance;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
