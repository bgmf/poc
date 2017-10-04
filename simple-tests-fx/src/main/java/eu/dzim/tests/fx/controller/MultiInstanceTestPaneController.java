package eu.dzim.tests.fx.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MultiInstanceTestPaneController {
	
	@FXML private VBox test;
	@FXML private Label header;
	@FXML private Button button1;
	
	private String id;
	
	@FXML
	protected void initialize() {
		
	}
	
	public void updateIDAndHeader(String id, String text) {
		this.id = id;
		header.setText(text);
	}
	
	@FXML
	public void handleButton1(ActionEvent event) {
		System.err.println("I was pressed on Pane with ID " + id);
	}
}
