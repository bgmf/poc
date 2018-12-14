package eu.dzim.shared.fx.util;

import eu.dzim.shared.resource.Resource;
import eu.dzim.shared.util.BaseEnumType;
import javafx.scene.control.ListCell;

public class EnumListCell<T extends BaseEnumType> extends ListCell<T> {

    private final Resource resource;

    public EnumListCell(final Resource resource) {
        this.resource = resource;
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty ? "" : resource.getGuaranteedString(item.getKey()));
    }
}
