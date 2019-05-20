package eu.dzim.tests.fx.controller;

import eu.dzim.tests.fx.MainWithControllerFactory2.Model;
import eu.dzim.tests.fx.MainWithControllerFactory2.ModelInjector;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class FactoryInjectionController2 implements ModelInjector {

    @FXML private Button increaseButton;

    private Model model;

    /*
     * to bind to sth within the controller, you need properties
     * and you need all accessor methods - for whatever reason
     */
    private StringProperty textBinder = new SimpleStringProperty(this, "textBinder", "TEST");
    public String getTextBinder() { return textBinder.get(); }
    public void setTextBinder(String value) { textBinder.set(value); }
    public StringProperty textBinderProperty() { return textBinder; }

    // if you have something like the model, not wrapped into a ObjectProperty or so,
    // create additional property accessor methods for the required properties
    public String getText() { return model.textProperty().get(); }
    public void setText(String value) { model.setText(value); }
    public StringProperty textProperty() { return model.textProperty(); }

    // wrap your model, if you want direct access
    private ObjectProperty<Model> modelWrapper = new SimpleObjectProperty<>(this, "modelWrapper", null);
    public Model getModelWrapper() { return modelWrapper.get(); }
    public void setModelWrapper(Model modelWrapper) { this.modelWrapper.set(modelWrapper); }
    public ObjectProperty<Model> modelObjectProperty() { return modelWrapper; }

    @Override
    public void setModel(Model model) {
        System.out.println(getClass().getName() + " is receiving model.");
        this.model = model;
        textBinder.bind(model.textProperty());
        setModelWrapper(model);
    }

    @FXML
    private void initialize() {
        System.out.println(getClass().getName() + " is initializing.");
    }

    @FXML
    private void increase(ActionEvent actionEvent) {
        model.increaseCounter();
        actionEvent.consume();
    }
}
