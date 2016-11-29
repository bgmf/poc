package eu.dzim.tests.fx;

import javafx.fxml.FXML;

public class View1Controller implements ExampleInterface {
	
	private ExampleModel model;
	
	public View1Controller() {
		// explicit zero-argument constructor for class instantiation
	}
	
	@Override
	public void setModel(ExampleModel model) {
		this.model = model;
	}
	
	@FXML
	protected void initialize() {
		// do something (e.g. with the model)
		// executed *after* the model is set
		System.err.println(toString() + ": model -> " + model);
	}
}
