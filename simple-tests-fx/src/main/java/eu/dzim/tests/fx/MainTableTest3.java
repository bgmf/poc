package eu.dzim.tests.fx;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class MainTableTest3 extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        StringProperty appStatus = new SimpleStringProperty();

        primaryStage.setTitle("Table Test 3");
        primaryStage.titleProperty().bind(Bindings
                .createStringBinding(() -> "TableTest3" + (appStatus.get() != null && !appStatus.get().isEmpty() ? " - " + appStatus.get() : ""),
                        appStatus));

        BorderPane rootLayout = new BorderPane();
        TableView<TableRecord> table = new TableView<>();

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

    private ObservableList<TableRecord> createRecords() {
        ObservableList<TableRecord> records = FXCollections.observableArrayList();
        records.add(new TableRecord("Test1", "ASDF1", 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0));
        records.add(new TableRecord("Test2", "ASDF2", 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0));
        records.add(new TableRecord("Test3", "ASDF3", 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0));
        records.add(new TableRecord("Test4", "ASDF4", 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0));
        records.add(new TableRecord("Test5", "ASDF5", 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0));
        records.add(new TableRecord("Test6", "ASDF6", 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0));
        records.add(new TableRecord("Test7", "ASDF7", 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0));
        records.add(new TableRecord("Test8", "ASDF8", 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0));
        records.add(new TableRecord("Test9", "ASDF9", 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0));
        records.add(new TableRecord("Test0", "ASDF0", 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0));
        return records;
    }

    private void createTableData(TableView<TableRecord> table, ObservableList<TableRecord> records) {

        List<TableColumn<TableRecord, ?>> columns = new ArrayList<>();
        columns.add(createColumn(String.class, "Name", "userName"));
        columns.add(createColumn(String.class, "Bezeichnung", "azlWert"));
        columns.add(createColumn(Double.class, "Januar", "azlJan"));
        columns.add(createColumn(Double.class, "Februar", "azlFeb"));
        columns.add(createColumn(Double.class, "März", "azlMar"));
        columns.add(createColumn(Double.class, "April", "azlApr"));
        columns.add(createColumn(Double.class, "Mai", "azlMai"));
        columns.add(createColumn(Double.class, "Juni", "azlJun"));
        columns.add(createColumn(Double.class, "Juli", "azlJul"));
        columns.add(createColumn(Double.class, "August", "azlAug"));
        columns.add(createColumn(Double.class, "September", "azlSep"));
        columns.add(createColumn(Double.class, "Oktober", "azlOkt"));
        columns.add(createColumn(Double.class, "November", "azlNov"));
        columns.add(createColumn(Double.class, "Dezember", "azlDez"));

        table.getColumns().addAll(columns);
        table.setItems(records);
    }

    private <T> TableColumn<TableRecord, T> createColumn(Class<T> columnType, String columnTitle, String propertyName) {
        TableColumn<TableRecord, T> column = new TableColumn<>(columnTitle);
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        return column;
    }

    public class TableRecord {

        private SimpleStringProperty userName, azlWert;
        private SimpleDoubleProperty azlJan, azlFeb, azlMar, azlApr, azlMai, azlJun, azlJul, azlAug, azlSep, azlOkt, azlNov, azlDez;

        public TableRecord(String userName, String azlWert, Double azlJan, Double azlFeb, Double azlMar, Double azlApr, Double azlMai, Double azlJun,
                Double azlJul, Double azlAug, Double azlSep, Double azlOkt, Double azlNov, Double azlDez) {
            this.userName = new SimpleStringProperty(userName);
            this.azlWert = new SimpleStringProperty(azlWert);
            this.azlJan = new SimpleDoubleProperty(azlJan);
            this.azlFeb = new SimpleDoubleProperty(azlFeb);
            this.azlMar = new SimpleDoubleProperty(azlMar);
            this.azlApr = new SimpleDoubleProperty(azlApr);
            this.azlMai = new SimpleDoubleProperty(azlMai);
            this.azlJun = new SimpleDoubleProperty(azlJun);
            this.azlJul = new SimpleDoubleProperty(azlJul);
            this.azlAug = new SimpleDoubleProperty(azlAug);
            this.azlSep = new SimpleDoubleProperty(azlSep);
            this.azlOkt = new SimpleDoubleProperty(azlOkt);
            this.azlNov = new SimpleDoubleProperty(azlNov);
            this.azlDez = new SimpleDoubleProperty(azlDez);
        }

        public final SimpleStringProperty userNameProperty() {
            return this.userName;
        }

        public final String getUserName() {
            return this.userNameProperty().get();
        }

        public final void setUserName(final String userName) {
            this.userNameProperty().set(userName);
        }

        public final SimpleStringProperty azlWertProperty() {
            return this.azlWert;
        }

        public final String getAzlWert() {
            return this.azlWertProperty().get();
        }

        public final void setAzlWert(final String azlWert) {
            this.azlWertProperty().set(azlWert);
        }

        public final SimpleDoubleProperty azlJanProperty() {
            return this.azlJan;
        }

        public final double getAzlJan() {
            return this.azlJanProperty().get();
        }

        public final void setAzlJan(final double azlJan) {
            this.azlJanProperty().set(azlJan);
        }

        public final SimpleDoubleProperty azlFebProperty() {
            return this.azlFeb;
        }

        public final double getAzlFeb() {
            return this.azlFebProperty().get();
        }

        public final void setAzlFeb(final double azlFeb) {
            this.azlFebProperty().set(azlFeb);
        }

        public final SimpleDoubleProperty azlMarProperty() {
            return this.azlMar;
        }

        public final double getAzlMar() {
            return this.azlMarProperty().get();
        }

        public final void setAzlMar(final double azlMar) {
            this.azlMarProperty().set(azlMar);
        }

        public final SimpleDoubleProperty azlAprProperty() {
            return this.azlApr;
        }

        public final double getAzlApr() {
            return this.azlAprProperty().get();
        }

        public final void setAzlApr(final double azlApr) {
            this.azlAprProperty().set(azlApr);
        }

        public final SimpleDoubleProperty azlMaiProperty() {
            return this.azlMai;
        }

        public final double getAzlMai() {
            return this.azlMaiProperty().get();
        }

        public final void setAzlMai(final double azlMai) {
            this.azlMaiProperty().set(azlMai);
        }

        public final SimpleDoubleProperty azlJunProperty() {
            return this.azlJun;
        }

        public final double getAzlJun() {
            return this.azlJunProperty().get();
        }

        public final void setAzlJun(final double azlJun) {
            this.azlJunProperty().set(azlJun);
        }

        public final SimpleDoubleProperty azlJulProperty() {
            return this.azlJul;
        }

        public final double getAzlJul() {
            return this.azlJulProperty().get();
        }

        public final void setAzlJul(final double azlJul) {
            this.azlJulProperty().set(azlJul);
        }

        public final SimpleDoubleProperty azlAugProperty() {
            return this.azlAug;
        }

        public final double getAzlAug() {
            return this.azlAugProperty().get();
        }

        public final void setAzlAug(final double azlAug) {
            this.azlAugProperty().set(azlAug);
        }

        public final SimpleDoubleProperty azlSepProperty() {
            return this.azlSep;
        }

        public final double getAzlSep() {
            return this.azlSepProperty().get();
        }

        public final void setAzlSep(final double azlSep) {
            this.azlSepProperty().set(azlSep);
        }

        public final SimpleDoubleProperty azlOktProperty() {
            return this.azlOkt;
        }

        public final double getAzlOkt() {
            return this.azlOktProperty().get();
        }

        public final void setAzlOkt(final double azlOkt) {
            this.azlOktProperty().set(azlOkt);
        }

        public final SimpleDoubleProperty azlNovProperty() {
            return this.azlNov;
        }

        public final double getAzlNov() {
            return this.azlNovProperty().get();
        }

        public final void setAzlNov(final double azlNov) {
            this.azlNovProperty().set(azlNov);
        }

        public final SimpleDoubleProperty azlDezProperty() {
            return this.azlDez;
        }

        public final double getAzlDez() {
            return this.azlDezProperty().get();
        }

        public final void setAzlDez(final double azlDez) {
            this.azlDezProperty().set(azlDez);
        }
    }
}