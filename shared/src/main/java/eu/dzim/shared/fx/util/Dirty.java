package eu.dzim.shared.fx.util;

import javafx.beans.property.BooleanProperty;

public interface Dirty {

    boolean isDirty();

    void setDirty(boolean dirty);

    BooleanProperty dirtyProperty();
}
