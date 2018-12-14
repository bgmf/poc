package eu.dzim.shared.fx.util;

import javafx.scene.control.TextField;

import java.util.concurrent.Callable;

public class BooleanCallable implements Callable<Boolean> {

    private final Dirtyable allowDirty;
    private final Dirty dirty;
    private final TextField textField;

    public BooleanCallable(final Dirtyable allowDirty, final Dirty dirty, final TextField textField) {
        this.allowDirty = allowDirty;
        this.dirty = dirty;
        this.textField = textField;
    }

    @Override
    public Boolean call() throws Exception {
        boolean value = false;
        value = Boolean.parseBoolean(textField.getText());
        if (allowDirty.isAllowDirty()) {
            dirty.setDirty(true);
        }
        return value;
    }
}