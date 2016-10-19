package eu.dzim.tests.fx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TableTest2Model {
	
	private StringProperty text = new SimpleStringProperty("");
	private BooleanProperty flag1 = new SimpleBooleanProperty(false);
	private BooleanProperty flag2 = new SimpleBooleanProperty(false);
	private BooleanProperty flag3 = new SimpleBooleanProperty(false);
	
	public final StringProperty textProperty() {
		return this.text;
	}
	
	public final String getText() {
		return this.textProperty().get();
	}
	
	public final void setText(final String text) {
		this.textProperty().set(text);
	}
	
	public final BooleanProperty flag1Property() {
		return this.flag1;
	}
	
	public final boolean isFlag1() {
		return this.flag1Property().get();
	}
	
	public final void setFlag1(final boolean flag) {
		this.flag1Property().set(flag);
	}
	
	public final BooleanProperty flag2Property() {
		return this.flag2;
	}
	
	public final boolean isFlag2() {
		return this.flag2Property().get();
	}
	
	public final void setFlag2(final boolean flag) {
		this.flag2Property().set(flag);
	}
	
	public final BooleanProperty flag3Property() {
		return this.flag3;
	}
	
	public final boolean isFlag3() {
		return this.flag3Property().get();
	}
	
	public final void setFlag3(final boolean flag) {
		this.flag3Property().set(flag);
	}
}
