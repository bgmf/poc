package eu.dzim.tests.fx;

import eu.dzim.shared.fx.ui.PseudoTable;
import eu.dzim.shared.fx.ui.PseudoTableCell;
import eu.dzim.shared.fx.ui.PseudoTableRow;
import eu.dzim.shared.util.SharedConstants;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainPseudoTable extends Application {
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		PseudoTable<String> table = new PseudoTable<>();
		BorderPane root = new BorderPane(table);
		BorderPane.setMargin(table, new Insets(5.0));
		
		// table.setSelectionType(SelectionType.COLUMN);
		// table.setSelectionType(SelectionType.ROW);
		// table.setSelectionType(SelectionType.CELL);
		// table.setSelectionType(SelectionType.NONE);
		
		table.setItemSelectedHandler(item -> System.err.println("### ITEM: " + item));
		table.setRowSelectedHandler(row -> System.err.println("### ROW: " + row));
		table.setCellSelectedHandler(cell -> System.err.println("### CELL: " + cell));
		
		table.getFirstRow().setText("Row Names");
		PseudoTableRow<String> row1 = new PseudoTableRow<>("Row 1");
		PseudoTableRow<String> row2 = new PseudoTableRow<>("Row 2");
		PseudoTableRow<String> row3 = new PseudoTableRow<>("Row 3");
		PseudoTableRow<String> row4 = new PseudoTableRow<>("Row 4");
		PseudoTableRow<String> row5 = new PseudoTableRow<>("Row 5");
		PseudoTableRow<String> row6 = new PseudoTableRow<>("Row 6");
		//
		PseudoTableRow<String> row7 = new PseudoTableRow<>("Row 7");
		
		table.getLastRow().setCellFactory(row -> new PseudoTableCell<String>() {
			@Override
			protected void renderCell(String item) {
				// do nothing
			}
		});
		
		table.getRows().addAll(row1, row2, row3, row4, row5, row6);
		
		table.getItems().addAll("TEST", "TEST2");
		
		Scene scene = new Scene(root, 640, 480);
		scene.getStylesheets().add(SharedConstants.CORE_CSS);
		
		primaryStage.setScene(scene);
		
		primaryStage.show();
		
		new Thread(() -> {
			try {
				table.getFirstRow().setVisible(false);
				row1.setVisible(false);
				row6.setVisible(false);
				table.getLastRow().setVisible(false);
				Thread.sleep(1000);
				Platform.runLater(() -> row6.setVisible(true));
				Thread.sleep(1000);
				Platform.runLater(() -> table.getLastRow().setVisible(true));
				Thread.sleep(1000);
				Platform.runLater(() -> table.getFirstRow().setVisible(true));
				Thread.sleep(1000);
				Platform.runLater(() -> row1.setVisible(true));
				Thread.sleep(1000);
				Platform.runLater(() -> table.getRows().add(row7));
				Thread.sleep(1000);
				Platform.runLater(() -> table.getRows().remove(row7));
				Thread.sleep(1000);
				Platform.runLater(() -> table.getRows().remove(row4));
				Thread.sleep(1000);
				Platform.runLater(() -> table.getRows().remove(row1));
				Thread.sleep(1000);
				Platform.runLater(() -> table.getRows().add(row4));
				Thread.sleep(1000);
				Platform.runLater(() -> table.getRows().add(row1));
				// FIXME the style class update is still a bit bonkers
				// FIXME row & cell selection are meschuge, too
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		})
		// .start()
		;
	}
	
	public static void main(String[] args) {
		Application.launch(MainPseudoTable.class, args);
	}
}
