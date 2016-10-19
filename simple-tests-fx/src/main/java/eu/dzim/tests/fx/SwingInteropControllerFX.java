package eu.dzim.tests.fx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class SwingInteropControllerFX implements SwingInteropControllerInterface {
	
	@FXML
	private BorderPane root;
	@FXML
	private Button button;
	
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