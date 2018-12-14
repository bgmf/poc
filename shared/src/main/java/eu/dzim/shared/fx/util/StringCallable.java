package eu.dzim.shared.fx.util;

import javafx.scene.control.TextField;

import java.util.concurrent.Callable;

public class StringCallable implements Callable<String> {

    private final Dirtyable allowDirty;
    private final Dirty dirty;
    private final TextField textField;

    public StringCallable(final Dirtyable allowDirty, final Dirty dirty, final TextField textField) {
        this.allowDirty = allowDirty;
        this.dirty = dirty;
        this.textField = textField;
    }

    @Override
    public String call() throws Exception {
        String value = textField.getText();
        if (allowDirty.isAllowDirty()) {
            dirty.setDirty(true);
        }
        return value;
    }
}