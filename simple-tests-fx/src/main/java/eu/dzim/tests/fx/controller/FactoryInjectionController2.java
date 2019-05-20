package eu.dzim.tests.fx.controller;

import eu.dzim.tests.fx.MainWithControllerFactory2.Model;
import eu.dzim.tests.fx.MainWithControllerFactory2.ModelInjector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class FactoryInjectionController2 implements ModelInjector {

    @FXML private Button increaseButton;

    @FXML private Model model;

    @Override
    public void setModel(Model model) {
        System.out.println(getClass().getName() + " is receiving model.");
        this.model = model;
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
