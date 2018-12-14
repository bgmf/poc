package eu.dzim.tests.fx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

import java.lang.reflect.Field;
import java.util.Optional;

public class TabPaneExampleController {

    @FXML private BorderPane root;
    @FXML private TabPane tabPane;
    @FXML private Tab tab1;
    @FXML private Tab tab2;

    @FXML
    public void initialize() {
        // tabPane.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
        // Task<Void> task = new Task<Void>() {
        // @Override
        // protected Void call() throws Exception {
        // try {
        // Thread.sleep(100);
        // } catch (InterruptedException e) {
        // Thread.currentThread().interrupt();
        // e.printStackTrace();
        // }
        // return null;
        // }
        // };
        // task.setOnSucceeded(event -> getObject(tabPane.getSkin(), "tabHeaderArea")
        // .ifPresent(tabHeaderArea -> getObject(tabHeaderArea, "headersRegion").ifPresent(headersRegion -> {
        // StackPane stack = (StackPane) headersRegion;
        // stack.getChildrenUnmodifiable().forEach(tabHeaderSkin -> {
        // Optional<Object> tab = getObject(tabHeaderSkin, "tab");
        // if (n == tab.get()) {
        // tabHeaderSkin.toFront();
        // tabHeaderSkin.setTranslateY(10.0);
        // } else {
        // tabHeaderSkin.toBack();
        // tabHeaderSkin.setTranslateY(0.0);
        // }
        // });
        // })));
        // new Thread(task).start();
        // });
    }

    private Optional<Object> getObject(Object source, String fieldName) {
        Field field = null;
        try {
            field = source.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        if (field == null) {
            return Optional.empty();
        }
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        Object content = null;
        try {
            content = field.get(source);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        Optional<Object> opt = Optional.ofNullable(content);
        field.setAccessible(accessible);
        return opt;
    }
}
