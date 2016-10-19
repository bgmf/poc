package eu.dzim.test.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TestModel {
	
	private StringProperty testString = null;
	
	public TestModel() {
		// sonar
	}
	
	public final StringProperty testStringProperty() {
		if (this.testString == null) {
			this.testString = new SimpleStringProperty(this, "testString", null);
		}
		return this.testString;
	}
	
	public final String getTestString() {
		return this.testStringProperty().get();
	}
	
	public final void setTestString(final String testString) {
		this.testStringProperty().set(testString);
	}
}
