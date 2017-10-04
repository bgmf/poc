package eu.dzim.tests.fx.controller;

import eu.dzim.tests.fx.SwingInteropControllerInterface;
import eu.dzim.tests.fx.model.SwingInteropModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class SwingInteropControllerFX implements SwingInteropControllerInterface {
	
	@FXML private BorderPane root;
	@FXML private Button button;
	
	private SwingInteropModel model;
	
	@FXML
	protected void initialize() {
		System.out.println(model.getSomeString());
	}
	
	@Override
	public void setModel(SwingInteropModel model) {
		this.model = model;
	}
}
