package eu.dzim.shared.fx.ui.model;

import eu.dzim.shared.util.SharedConstants;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public abstract class BaseApplicationModel {
	
	private final ReadOnlyObjectWrapper<Object> object;
	
	private final BooleanProperty textSizeChangeAllowed = new SimpleBooleanProperty(this, "textSizeChangeAllowed", false);
	private final IntegerProperty textSize = new SimpleIntegerProperty(this, "textSize", SharedConstants.TEXT_SIZE_DEFAULT);
	
	protected BaseApplicationModel(final Object object) {
		this.object = new ReadOnlyObjectWrapper<>(object);
	}
	
	/*
	 * read-only: object (example usage)
	 */
	
	public final ReadOnlyObjectProperty<Object> objectProperty() {
		return this.object.getReadOnlyProperty();
	}
	
	public final Object getObject() {
		return this.object.get();
	}
	
	/*
	 * Text size
	 */
	
	public final BooleanProperty textSizeChangeAllowedProperty() {
		return this.textSizeChangeAllowed;
	}
	
	public final boolean isTextSizeChangeAllowed() {
		return this.textSizeChangeAllowedProperty().get();
	}
	
	public final void setTextSizeChangeAllowed(final boolean textSizeChangeAllowed) {
		this.textSizeChangeAllowedProperty().set(textSizeChangeAllowed);
	}
	
	public final IntegerProperty textSizeProperty() {
		return this.textSize;
	}
	
	public final int getTextSize() {
		return this.textSizeProperty().get();
	}
	
	public final void setTextSize(final int textSize) {
		this.textSizeProperty().set(textSize);
	}
}
