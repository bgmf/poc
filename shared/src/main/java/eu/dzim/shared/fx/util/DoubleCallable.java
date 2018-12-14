package eu.dzim.shared.fx.util;

import javafx.scene.control.TextField;

import java.util.concurrent.Callable;

public class DoubleCallable implements Callable<Double> {

    private final Dirtyable allowDirty;
    private final Dirty dirty;
    private final TextField textField;

    public DoubleCallable(final Dirtyable allowDirty, final Dirty dirty, final TextField textField) {
        this.allowDirty = allowDirty;
        this.dirty = dirty;
        this.textField = textField;
    }

    @Override
    public Double call() throws Exception {
        double value = 0;
        try {
            value = Double.parseDouble(textField.getText());
            if (allowDirty.isAllowDirty()) {
                dirty.setDirty(true);
            }
            return value;
        } catch (NumberFormatException e) {
            return value;
        }
    }
}