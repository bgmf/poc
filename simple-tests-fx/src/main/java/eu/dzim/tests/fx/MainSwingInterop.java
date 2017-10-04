package eu.dzim.tests.fx;

import eu.dzim.tests.fx.model.SwingInteropModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainSwingInterop extends Application {
	
	private SwingInteropModel applicationModel = new SwingInteropModel();
	
	@Override
	public void start(Stage stage) throws Exception {
		
		applicationModel.setSomeString("SwingInterop");
		
		FXMLLoader loader = new FXMLLoader();
		loader.setControllerFactory(this::controllerForClass);
		loader.setLocation(getClass().getResource("SwingInteropSwingButton.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root, 1200, 800);
		
		stage.setScene(scene);
		stage.show();
	}
	
	private Object controllerForClass(Class<?> clazz) {
		try {
			Object controllerInstance = clazz.newInstance();
			if (controllerInstance instanceof SwingInteropControllerInterface) {
				((SwingInteropControllerInterface) controllerInstance).setModel(applicationModel);
			}
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
