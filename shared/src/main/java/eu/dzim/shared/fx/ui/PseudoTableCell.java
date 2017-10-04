package eu.dzim.shared.fx.ui;

import eu.dzim.shared.fx.ui.PseudoTable.SelectionType;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public abstract class PseudoTableCell<T> extends Label {
	
	private PseudoTableRow<T> row;
	
	private ObjectProperty<T> item = new SimpleObjectProperty<>(this, "item");
	
	public PseudoTableCell() {
		this(null);
	}
	
	public PseudoTableCell(PseudoTableRow<T> row) {
		this.row = row;
		initialize();
	}
	
	protected abstract void renderCell(T item);
	
	protected void handleCellSelection(T item, boolean mouse, MouseButton button, int count) {
		// override, if required
	}
	
	private void initialize() {
		setOnMouseClicked(this::handleSelection);
		setOnTouchReleased(this::handleSelection);
	}
	
	protected void handleSelection(Event event) {
		if (event == null || row == null || row.getTable() == null)
			return;
		
		int count = 0;
		boolean mouse = false;
		MouseButton button = null;
		if (event instanceof MouseEvent) {
			mouse = true;
			count = ((MouseEvent) event).getClickCount();
			button = ((MouseEvent) event).getButton();
		}
		
		PseudoTable<T> table = row.getTable();
		SelectionType type = table.getSelectionType();
		T item = getItem();
		switch (type) {
		case COLUMN:
		case ROW:
		case CELL:
			table.setSelectionItem(item);
			table.setSelectionRow(row);
			table.setSelectionCell(this);
			if (SelectionType.COLUMN == type && item != null)
				table.getItemSelectedHandler().accept(item);
			if (SelectionType.ROW == type && row != null)
				table.getRowSelectedHandler().accept(row);
			if (SelectionType.CELL == type)
				table.getCellSelectedHandler().accept(this);
			break;
		case NONE:
		default:
			table.setSelectionItem(null);
			table.setSelectionRow(null);
			table.setSelectionCell(null);
			break;
		}
		handleCellSelection(item, mouse, button, count);
		event.consume();
	}
	
	public PseudoTableRow<T> getRow() {
		return row;
	}
	
	public void setRow(PseudoTableRow<T> row) {
		this.row = row;
	}
	
	public final ObjectProperty<T> itemProperty() {
		return item;
	}
	
	public final T getItem() {
		return itemProperty().get();
	}
	
	public final void setItem(T item) {
		itemProperty().set(item);
	}
}
