package eu.dzim.tests.fx;

import java.util.Locale;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class ListTestController {
	
	@FXML
	private ListView<TableTestObject> list;
	
	@FXML
	protected void initialize() {
		
		ObservableList<TableTestObject> data = FXCollections.observableArrayList();
		TableTestObject test = new TableTestObject();
		test.setText("Test");
		test.setFlag(true);
		test.setPoints(1);
		data.add(test);
		
		list.setItems(data);
		
		list.getSelectionModel().selectedItemProperty().addListener((observable, oldObject, newObject) -> {
			System.err.printf(Locale.ROOT, "selection: t[%s], f[%b], p[%d]%n", newObject.getText(), newObject.isFlag(), newObject.getPoints());
		});
		
		list.setCellFactory(param -> new ListCell<TableTestObject>() {
			protected void updateItem(TableTestObject item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setText(null);
				} else {
					setText(String.format(Locale.ROOT, "t[%s], f[%b], p[%d]", item.getText(), item.isFlag(), item.getPoints()));
				}
			};
		});
	}
}
