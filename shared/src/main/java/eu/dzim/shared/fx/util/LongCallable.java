package eu.dzim.shared.fx.util;

import java.util.concurrent.Callable;

import javafx.scene.control.TextField;

public class LongCallable implements Callable<Long> {
	
	private final Dirtyable allowDirty;
	private final Dirty dirty;
	private final TextField textField;
	
	public LongCallable(final Dirtyable allowDirty, final Dirty dirty, final TextField textField) {
		this.allowDirty = allowDirty;
		this.dirty = dirty;
		this.textField = textField;
	}
	
	@Override
	public Long call() throws Exception {
		long value = 0;
		try {
			value = Long.parseLong(textField.getText());
			if (allowDirty.isAllowDirty()) {
				dirty.setDirty(true);
			}
			return value;
		} catch (NumberFormatException e) {
			return value;
		}
	}
}