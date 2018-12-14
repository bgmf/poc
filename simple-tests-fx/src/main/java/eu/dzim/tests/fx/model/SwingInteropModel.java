package eu.dzim.tests.fx.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SwingInteropModel {

    private StringProperty someString;

    public final StringProperty someStringProperty() {
        if (this.someString == null)
            this.someString = new SimpleStringProperty();
        return this.someString;
    }

    public final String getSomeString() {
        return this.someStringProperty().get();
    }

    public final void setSomeString(final String stringProperty) {
        this.someStringProperty().set(stringProperty);
    }

}
