package eu.dzim.shared.fx.util;

import javafx.scene.control.ListCell;

public class IntegerListCell extends ListCell<Integer> {

    public IntegerListCell() {
        // for sonar
    }

    @Override
    protected void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty || item == null ? "" : item.toString());
    }
}
