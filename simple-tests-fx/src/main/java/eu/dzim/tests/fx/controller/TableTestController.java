package eu.dzim.tests.fx.controller;

import eu.dzim.tests.fx.model.TableTestObject;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.*;

public class TableTestController {

    @FXML private TableView<TableTestObject> tableView;
    @FXML private TableColumn<TableTestObject, String> tableColumnText;
    @FXML private TableColumn<TableTestObject, TableTestObject> tableColumnFlag;
    @FXML private TableColumn<TableTestObject, TableTestObject> tableColumnAncestor;
    @FXML private TableColumn<TableTestObject, TableTestObject> tableColumnDescendant;
    @FXML private TableColumn<TableTestObject, TableTestObject> tableColumnPoints;

    private Map<String, List<String>> descendantMap;
    private ObservableList<String> ancestorList;

    @FXML
    protected void initialize() {

        descendantMap = new HashMap<>();
        descendantMap.put("1", Arrays.asList("A", "B", "C"));
        descendantMap.put("2", Arrays.asList("Z", "L", "C"));
        descendantMap.put("3", Arrays.asList("A", "B", "R"));
        descendantMap.put("4", Arrays.asList("C", "B", "E"));
        descendantMap.put("5", Arrays.asList("A", "E", "C"));
        descendantMap.put("6", Arrays.asList("M", "V", "T"));
        descendantMap.put("7", Arrays.asList("A", "G", "F"));
        descendantMap.put("8", Arrays.asList("J", "O", "N"));
        descendantMap.put("9", Arrays.asList("X", "G", "E"));
        descendantMap.put("10", Arrays.asList("H", "I", "J"));

        ancestorList = FXCollections.observableArrayList(descendantMap.keySet());

        ObservableList<TableTestObject> data = FXCollections.observableArrayList();
        TableTestObject test = new TableTestObject();
        test.setText("Test");
        test.setFlag(true);
        test.setPoints(1);
        data.add(test);

        tableView.setItems(data);

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldObject, newObject) -> {
            System.err.printf(Locale.ROOT, "selection: t[%s], f[%b], p[%d]%n", newObject.getText(), newObject.isFlag(), newObject.getPoints());
        });

        tableColumnText.setCellValueFactory(new PropertyValueFactory<>("text"));
        tableColumnFlag.setCellValueFactory(param -> new SimpleObjectProperty<TableTestObject>(param.getValue()));
        tableColumnAncestor.setCellValueFactory(param -> new SimpleObjectProperty<TableTestObject>(param.getValue()));
        tableColumnDescendant.setCellValueFactory(param -> new SimpleObjectProperty<TableTestObject>(param.getValue()));
        tableColumnPoints.setCellValueFactory(param -> new SimpleObjectProperty<TableTestObject>(param.getValue()));

        tableColumnFlag.setCellFactory(param -> new TableCell<TableTestObject, TableTestObject>() {
            @Override
            protected void updateItem(TableTestObject item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    CheckBox cb = new CheckBox();
                    cb.selectedProperty().bindBidirectional(item.flagProperty());
                    setGraphic(cb);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                }
            }
        });
        tableColumnAncestor.setCellFactory(param -> new TableCell<TableTestObject, TableTestObject>() {
            @Override
            protected void updateItem(TableTestObject item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    ComboBox<String> cb = new ComboBox<>(ancestorList);
                    cb.getSelectionModel().selectedItemProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
                        item.ancestorProperty().set(newValue);
                        System.out.println(item.descendantProperty().get());
                    });
                    setGraphic(cb);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                }
            }
        });
        tableColumnDescendant.setCellFactory(param -> new TableCell<TableTestObject, TableTestObject>() {
            @Override
            protected void updateItem(TableTestObject item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    ComboBox<String> cb = new ComboBox<>();
                    item.ancestorProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
                        item.descendantProperty().set("");
                        cb.setItems(FXCollections.observableArrayList(descendantMap.get(newValue)));
                    });
                    cb.getSelectionModel().selectedItemProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
                        item.descendantProperty().set(newValue);
                        System.out.println(item.descendantProperty().get());
                    });
                    setGraphic(cb);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                }
            }
        });
        tableColumnPoints.setCellFactory(param -> new TableCell<TableTestObject, TableTestObject>() {
            @Override
            protected void updateItem(TableTestObject item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    Spinner<Integer> spinner = new Spinner<>();
                    spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-5, +5, item.getPoints()));
                    spinner.valueProperty().addListener((ChangeListener<Integer>) (observable, oldValue, newValue) -> {
                        item.pointsProperty().set(newValue);
                        System.out.println(item.pointsProperty().get());
                    });
                    setGraphic(spinner);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                }
            }
        });
    }
}
