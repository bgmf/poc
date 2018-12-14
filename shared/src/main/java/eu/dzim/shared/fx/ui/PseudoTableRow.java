package eu.dzim.shared.fx.ui;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PseudoTableRow<T> extends Label {

    private final Map<T, PseudoTableCell<T>> cellMapping = new HashMap<>();

    private PseudoTable<T> table;

    private Callback<PseudoTableRow<T>, PseudoTableCell<T>> cellFactory = createDefaultFactory();

    public PseudoTableRow() {
        this(null, null, null);
    }

    public PseudoTableRow(String text) {
        this(null, text, null);
    }

    public PseudoTableRow(String text, Node graphic) {
        this(null, text, graphic);
    }

    public PseudoTableRow(PseudoTable<T> table) {
        this(table, null, null);
    }

    public PseudoTableRow(PseudoTable<T> table, String text) {
        this(table, text, null);
    }

    public PseudoTableRow(PseudoTable<T> table, String text, Node graphic) {
        this.table = table;
        setText(text == null ? "" : text);
        setGraphic(graphic);
        managedProperty().bind(visibleProperty());
    }

    public PseudoTable<T> getTable() {
        return table;
    }

    public void setTable(PseudoTable<T> table) {
        this.table = table;
    }

    public final Callback<PseudoTableRow<T>, PseudoTableCell<T>> getCellFactory() {
        return cellFactory;
    }

    public final void setCellFactory(final Callback<PseudoTableRow<T>, PseudoTableCell<T>> cellFactory) {
        this.cellFactory = cellFactory == null ? createDefaultFactory() : cellFactory;
    }

    public final Optional<PseudoTableCell<T>> findCell(T item) {
        return Optional.ofNullable(cellMapping.get(item));
    }

    protected final Optional<PseudoTableCell<T>> getCell(T item) {
        Optional<PseudoTableCell<T>> opt = findCell(item);
        if (opt.isPresent())
            return opt;
        PseudoTableCell<T> cell = createCellFor(item);
        cellMapping.put(item, cell);
        return Optional.of(cell);
    }

    protected final PseudoTableCell<T> removeCell(T item) {
        return cellMapping.remove(item);
    }

    private final PseudoTableCell<T> createCellFor(T item) {
        PseudoTableCell<T> cell = getCellFactory().call(this);
        cell.managedProperty().bind(managedProperty());
        cell.visibleProperty().bind(visibleProperty());
        cell.setRow(this);
        cell.setItem(item);
        cell.renderCell(item);
        return cell;
    }

    private final Callback<PseudoTableRow<T>, PseudoTableCell<T>> createDefaultFactory() {
        return row -> new PseudoTableCell<T>() {
            @Override
            protected void renderCell(T item) {
                if (item == null) {
                    super.setText(null);
                    super.setGraphic(null);
                } else if (item instanceof Node) {
                    super.setText(null);
                    super.setGraphic((Node) item);
                } else {
                    super.setText(item.toString());
                    super.setGraphic(null);
                }
            }
        };
    }
}
