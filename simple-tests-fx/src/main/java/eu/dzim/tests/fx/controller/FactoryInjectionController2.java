package eu.dzim.tests.fx.controller;

import eu.dzim.tests.fx.MainWithControllerFactory2;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class FactoryInjectionController2 implements MainWithControllerFactory2.ModelInjector {

    @FXML private Button increaseButton;

    private MainWithControllerFactory2.Model model = null;

    @Override
    public void setModel(MainWithControllerFactory2.Model model) {
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
