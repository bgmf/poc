package eu.dzim.shared.fx.ui;

import java.util.function.BiConsumer;

public class PseudoTableECell<T> extends PseudoTableCell<T> {
	
	private final BiConsumer<PseudoTableCell<T>, T> cellRenderer;
	
	public PseudoTableECell(final BiConsumer<PseudoTableCell<T>, T> cellRenderer) {
		this(null, cellRenderer);
	}
	
	public PseudoTableECell(PseudoTableRow<T> row, final BiConsumer<PseudoTableCell<T>, T> cellRenderer) {
		super(row);
		this.cellRenderer = cellRenderer;
	}
	
	@Override
	protected void renderCell(T item) {
		cellRenderer.accept(this, item);
	}
}
