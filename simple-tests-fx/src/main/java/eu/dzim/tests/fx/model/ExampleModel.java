package eu.dzim.tests.fx.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ExampleModel {
	
	private StringProperty name = new SimpleStringProperty(this, "name", "");
	
	public ExampleModel() {}
	
	public final StringProperty nameProperty() {
		return this.name;
	}
	
	public final String getName() {
		return this.nameProperty().get();
	}
	
	public final void setName(final String name) {
		this.nameProperty().set(name);
	}
}
