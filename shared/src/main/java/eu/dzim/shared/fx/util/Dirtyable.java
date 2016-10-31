package eu.dzim.shared.fx.util;

import javafx.beans.property.ReadOnlyBooleanProperty;

public interface Dirtyable {
	
	ReadOnlyBooleanProperty allowDirtyProperty();
	
	boolean isAllowDirty();
	
	void setAllowDirty(boolean dirty);
}
