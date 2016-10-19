package eu.dzim.tests.fx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TableTestObject {
	
	private StringProperty text = new SimpleStringProperty("");
	private BooleanProperty flag = new SimpleBooleanProperty(false);
	private StringProperty ancestor = new SimpleStringProperty("");
	private StringProperty descendant = new SimpleStringProperty("");
	private IntegerProperty points = new SimpleIntegerProperty(0);
	
	public final StringProperty textProperty() {
		return this.text;
	}
	
	public final String getText() {
		return this.textProperty().get();
	}
	
	public final void setText(final String text) {
		this.textProperty().set(text);
	}
	
	public final BooleanProperty flagProperty() {
		return this.flag;
	}
	
	public final boolean isFlag() {
		return this.flagProperty().get();
	}
	
	public final void setFlag(final boolean flag) {
		this.flagProperty().set(flag);
	}
	
	public final StringProperty ancestorProperty() {
		return this.ancestor;
	}
	
	public final String getAncestor() {
		return this.ancestorProperty().get();
	}
	
	public final void setAncestor(final String ancestor) {
		this.ancestorProperty().set(ancestor);
	}
	
	public final StringProperty descendantProperty() {
		return this.descendant;
	}
	
	public final String getDescendant() {
		return this.descendantProperty().get();
	}
	
	public final void setDescendant(final String descendant) {
		this.descendantProperty().set(descendant);
	}
	
	public final IntegerProperty pointsProperty() {
		return this.points;
	}
	
	public final int getPoints() {
		return this.pointsProperty().get();
	}
	
	public final void setPoints(final int points) {
		this.pointsProperty().set(points);
	}
}
