package eu.dzim.shared.fx.util;

import javafx.scene.control.TextField;

import java.util.concurrent.Callable;

public class IntegerCallable implements Callable<Integer> {

    private final Dirtyable allowDirty;
    private final Dirty dirty;
    private final TextField textField;

    public IntegerCallable(final Dirtyable allowDirty, final Dirty dirty, final TextField textField) {
        this.allowDirty = allowDirty;
        this.dirty = dirty;
        this.textField = textField;
    }

    @Override
    public Integer call() throws Exception {
        int value = 0;
        try {
            value = Integer.parseInt(textField.getText());
            if (allowDirty.isAllowDirty()) {
                dirty.setDirty(true);
            }
            return value;
        } catch (NumberFormatException e) {
            return value;
        }
    }
}