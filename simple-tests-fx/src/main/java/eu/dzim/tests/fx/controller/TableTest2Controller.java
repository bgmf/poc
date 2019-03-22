package eu.dzim.tests.fx.controller;

import eu.dzim.tests.fx.model.TableTest2Model;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class TableTest2Controller {

    @FXML private TabPane tabPane;
    @FXML private Tab tab1;
    @FXML private TableView<TableTest2Model> tableView;
    @FXML private TableColumn<TableTest2Model, String> tableColumnText;
    @FXML private TableColumn<TableTest2Model, Boolean> tableColumnFlag1;
    @FXML private TableColumn<TableTest2Model, Boolean> tableColumnFlag2;
    @FXML private TableColumn<TableTest2Model, Boolean> tableColumnFlag3;
    @FXML private Tab tab2;
    @FXML private TextField textField;
    @FXML private CheckBox checkBox1;
    @FXML private CheckBox checkBox2;
    @FXML private CheckBox checkBox3;
    @FXML private Button button;

    @FXML
    private void initialize() {

        System.err.println("#init");

        tableView.setItems(FXCollections.observableArrayList());

        tableColumnText.setCellValueFactory(new PropertyValueFactory<>("text"));
        tableColumnFlag1.setCellValueFactory(new PropertyValueFactory<>("flag1"));
        tableColumnFlag2.setCellValueFactory(new PropertyValueFactory<>("flag2"));
        tableColumnFlag3.setCellValueFactory(new PropertyValueFactory<>("flag3"));

        button.setOnAction(actionEvent -> {

            TableTest2Model model = new TableTest2Model();
            model.setText(textField.getText());
            model.setFlag1(checkBox1.isSelected());
            model.setFlag2(checkBox2.isSelected());
            model.setFlag3(checkBox3.isSelected());

            tableView.getItems().add(model);

            actionEvent.consume();
        });
    }
}
