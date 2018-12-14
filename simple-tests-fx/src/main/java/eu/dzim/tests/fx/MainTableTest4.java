package eu.dzim.tests.fx;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainTableTest4 extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        StringProperty appStatus = new SimpleStringProperty();

        primaryStage.setTitle("TableTest4");
        primaryStage.titleProperty().bind(Bindings
                .createStringBinding(() -> "TableTest4" + (appStatus.get() != null && !appStatus.get().isEmpty() ? " - " + appStatus.get() : ""),
                        appStatus));

        BorderPane rootLayout = new BorderPane();
        TableView<Map<String, Object>> table = new TableView<>();

        createTableData(table, createRecords());

        appStatus.bind(Bindings.createStringBinding(() -> {
            int size = table.getItems().size();
            String str = size > 1 ? " Einträge" : " Eintrag";
            return size + str;
        }, table.getItems()));

        rootLayout.setCenter(table);
        Scene scene = new Scene(rootLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private ObservableList<Map<String, Object>> createRecords() {
        ObservableList<Map<String, Object>> records = FXCollections.observableArrayList();
        Map<String, Object> map = new TreeMap<>();
        map.put("1", "Eins");
        map.put("2", 2.0);
        map.put("3", true);
        records.add(map);
        map = new TreeMap<>();
        map.put("1", "Eins");
        map.put("2", 2.0);
        map.put("4", 42);
        records.add(map);
        return records;
    }

    private void createTableData(TableView<Map<String, Object>> table, ObservableList<Map<String, Object>> records) {

        table.getItems().clear();
        table.getColumns().clear();

        Map<String, Class<?>> header = new TreeMap<>();
        records.forEach(map -> map.keySet().forEach(key -> header.put(key, map.get(key) == null ? Object.class : map.get(key).getClass())));

        List<TableColumn<Map<String, Object>, ?>> columns = new ArrayList<>();
        header.forEach((col, clazz) -> columns.add(createColumn(clazz, col, col)));

        table.getColumns().addAll(columns);
        table.setItems(records);
    }

    private <T> TableColumn<Map<String, Object>, ?> createColumn(Class<T> columnType, String columnTitle, String propertyName) {
        TableColumn<Map<String, Object>, Object> column = new TableColumn<>(columnTitle);
        column.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().get(propertyName)));
        // TODO this approach uses each values #toString() method
        // equivalent to the following snippet
        // column.setCellFactory(param -> new TableCell<Map<String, Object>, Object>() {
        // 	protected void updateItem(Object item, boolean empty) {
        // 		if (empty || item == null) setText(null); else setText(item.toString());
        // 	};
        // });
        return column;
    }
}