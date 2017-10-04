package eu.dzim.tests.fx.controller;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import eu.dzim.tests.fx.model.ExampleInterface;
import eu.dzim.tests.fx.model.ExampleModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class RootController {
	
	private final ExampleModel model;
	
	@FXML private BorderPane root;
	
	public RootController() {
		this.model = new ExampleModel();
	}
	
	@FXML
	public void initialize() {
		// if we need something to do, e.g. translations (i18n/l10n)
		showView1();
	}
	
	@FXML
	public void exitApplication(ActionEvent event) {
		// orderly shutdown the JavaFX application this view belongs to
		Platform.exit();
	}
	
	@FXML
	public void showView1() {
		// verbose example
		Optional<Pane> optContent = loadCenterContent("View1");
		if (optContent.isPresent())
			root.setCenter(optContent.get());
			
		// Java 8 schortcut with Lambda-Expression
		// loadCenterContent("View1").ifPresent(pane -> root.setCenter(pane));
	}
	
	@FXML
	public void showView2() {
		loadCenterContent("View2").ifPresent(pane -> root.setCenter(pane));
	}
	
	private Optional<Pane> loadCenterContent(String fxmlFilename) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFilename + ".fxml"));
		Pane pane = null;
		try {
			// don't panic, it's only a lambda expression
			loader.setControllerFactory(param -> {
				try {
					Constructor<?> zeroArgumentConstructor = param.getConstructor();
					// create new View[1-2]Controller instance
					Object controller = zeroArgumentConstructor.newInstance();
					if (controller instanceof ExampleInterface)
						((ExampleInterface) controller).setModel(model); // inject the model
					return controller;
				} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					e.printStackTrace();
				}
				return null;
			});
			pane = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Optional.ofNullable(pane);
	}
}
