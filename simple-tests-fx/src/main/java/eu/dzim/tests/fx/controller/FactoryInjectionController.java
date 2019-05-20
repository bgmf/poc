package eu.dzim.tests.fx.controller;

import eu.dzim.tests.fx.MainWithControllerFactory2.Model;
import eu.dzim.tests.fx.MainWithControllerFactory2.ModelInjector;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class FactoryInjectionController implements ModelInjector {

    @FXML private VBox view2;
    @FXML private Object view2Controller;

    @FXML private Label counterLabel;

    @FXML private Model model;

    @Override
    public void setModel(Model model) {
        System.out.println(getClass().getName() + " is receiving model.");
        this.model = model;
    }

    @FXML
    private void initialize() {
        System.out.println(getClass().getName() + " is initializing.");
        System.out.println("has a sub-view: " + view2 + " -> " + view2Controller);
        counterLabel.textProperty().bind(model.counterProperty().asString());
    }

}
