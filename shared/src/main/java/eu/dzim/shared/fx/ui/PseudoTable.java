package eu.dzim.shared.fx.ui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import eu.dzim.shared.util.SharedConstants;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PseudoTable<T> extends BorderPane {
	
	/*
	 * TEST ONLY!
	 */
	public static class PseudoTableMain extends Application {
		
		@SuppressWarnings("unchecked")
		@Override
		public void start(Stage primaryStage) throws Exception {
			
			PseudoTable<String> table = new PseudoTable<>();
			BorderPane root = new BorderPane(table);
			BorderPane.setMargin(table, new Insets(5.0));
			
			// table.setSelectionType(SelectionType.COLUMN);
			// table.setSelectionType(SelectionType.ROW);
			// table.setSelectionType(SelectionType.CELL);
			// table.setSelectionType(SelectionType.NONE);
			
			table.setItemSelectedHandler(item -> System.err.println("### ITEM: " + item));
			table.setRowSelectedHandler(row -> System.err.println("### ROW: " + row));
			table.setCellSelectedHandler(cell -> System.err.println("### CELL: " + cell));
			
			table.getFirstRow().setText("Row Names");
			PseudoTableRow<String> row1 = new PseudoTableRow<>("Row 1");
			PseudoTableRow<String> row2 = new PseudoTableRow<>("Row 2");
			PseudoTableRow<String> row3 = new PseudoTableRow<>("Row 3");
			PseudoTableRow<String> row4 = new PseudoTableRow<>("Row 4");
			PseudoTableRow<String> row5 = new PseudoTableRow<>("Row 5");
			PseudoTableRow<String> row6 = new PseudoTableRow<>("Row 6");
			//
			PseudoTableRow<String> row7 = new PseudoTableRow<>("Row 7");
			
			table.getLastRow().setCellFactory(row -> new PseudoTableCell<String>() {
				@Override
				protected void renderCell(String item) {
					// do nothing
				}
			});
			
			table.getRows().addAll(row1, row2, row3, row4, row5, row6);
			
			table.getItems().addAll("TEST", "TEST2");
			
			Scene scene = new Scene(root, 640, 480);
			scene.getStylesheets().add(SharedConstants.CORE_CSS);
			
			primaryStage.setScene(scene);
			
			primaryStage.show();
			
			new Thread(() -> {
				try {
					table.getFirstRow().setVisible(false);
					row1.setVisible(false);
					row6.setVisible(false);
					table.getLastRow().setVisible(false);
					Thread.sleep(1000);
					Platform.runLater(() -> row6.setVisible(true));
					Thread.sleep(1000);
					Platform.runLater(() -> table.getLastRow().setVisible(true));
					Thread.sleep(1000);
					Platform.runLater(() -> table.getFirstRow().setVisible(true));
					Thread.sleep(1000);
					Platform.runLater(() -> row1.setVisible(true));
					Thread.sleep(1000);
					Platform.runLater(() -> table.getRows().add(row7));
					Thread.sleep(1000);
					Platform.runLater(() -> table.getRows().remove(row7));
					Thread.sleep(1000);
					Platform.runLater(() -> table.getRows().remove(row4));
					Thread.sleep(1000);
					Platform.runLater(() -> table.getRows().remove(row1));
					Thread.sleep(1000);
					Platform.runLater(() -> table.getRows().add(row4));
					Thread.sleep(1000);
					Platform.runLater(() -> table.getRows().add(row1));
					// FIXME the style class update is still a bit bonkers
					// FIXME row & cell selection are meschuge, too
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			})
			// .start()
			;
		}
		
		public static void main(String[] args) {
			Application.launch(PseudoTableMain.class, args);
		}
	}
	/*
	 * REMOVE THE TEST CLASS
	 */
	
	public static enum SelectionType {
		NONE, COLUMN, ROW, CELL
	}
	
	// private static final Logger LOG = LogManager.getLogger(PseudoTable.class);
	
	protected static final String PROP_ITEM = "pseudotable.prop.item";
	protected static final String PROP_INDEX = "pseudotable.prop.index";
	protected static final String PROP_INDEX_BINDING = "pseudotable.prop.indexBinding";
	
	// /ch/cnlab/aschwanden/ui/pseudo-table.css
	public static final String CSS_FILE = SharedConstants.CORE_RESOURCE_PATH + "css/pseudo-table.css";
	
	public static final String SC_DEFAULT_SCROLL_PANE = "scroll-pane";
	
	public static final String SC_PSEUDO_TABLE = "pseudo-table";
	public static final String SC_FIRST_COLUMN = "first-column";
	public static final String SC_CELL_TEXT = "cell-text";
	public static final String SC_SCROLL_PANE = "custom-scroll-pane";
	public static final String SC_FIRST_ROW = "first";
	public static final String SC_LAST_ROW = "last";
	public static final String SC_COLUMN_SCROLL_CONTAINER = "column-scroll-container";
	public static final String SC_COLUMN_BOX = "column-box";
	public static final String SC_COLUMN_CONTAINER = "column-container";
	public static final String SC_COLUMN = "column";
	public static final String SC_COLUMN_ROW_BOX = "row-box";
	
	private static final String CSS_SELECTOR_SCROLLBAR_VERTICAL = ".scroll-bar:vertical";
	// private static final String CSS_SELECTOR_SCROLLBAR_HORIZONTAL = ".scroll-bar:horizontal";
	// private static final String CSS_STYLE_SPECIAL_SP = "-fx-background-color: transparent;";
	// private static final String CSS_STYLE_SPECIAL_VP = "-fx-border-width: 0.0;";
	private static final String CSS_STYLE_SPECIAL_LAST_ROW = "-fx-min-height: 51.0;";
	
	private final PseudoClass selectedClass = PseudoClass.getPseudoClass("selected");
	private final PseudoClass selectedCellClass = PseudoClass.getPseudoClass("selected-cell");
	
	private final ObjectProperty<SelectionType> selectionType = new SimpleObjectProperty<>(this, "selectionType", SelectionType.COLUMN);
	private ObjectProperty<T> selectionItem = new SimpleObjectProperty<>(this, "selectionItem", null);
	private ObjectProperty<PseudoTableRow<T>> selectionRow = new SimpleObjectProperty<>(this, "selectionRow", null);
	private ObjectProperty<PseudoTableCell<T>> selectionCell = new SimpleObjectProperty<>(this, "selectionCell", null);
	
	private Consumer<T> itemSelectedHandler = item -> {};
	private Consumer<PseudoTableRow<T>> rowSelectedHandler = row -> {};
	private Consumer<PseudoTableCell<T>> cellSelectedHandler = cell -> {};
	
	private ObservableList<T> items = FXCollections.observableArrayList();
	
	private PseudoTableRow<T> firstRow = new PseudoTableRow<>();
	private ObservableList<PseudoTableRow<T>> rows = FXCollections.observableArrayList();
	private PseudoTableRow<T> lastRow = new PseudoTableRow<>();
	
	private VBox rowBox;
	private StackPane rowContainer;
	private ScrollBar rowScrollBar;
	private ScrollPane rowScrollPane;
	private VBox rowContent;
	
	private ScrollPane columnScrollPane;
	private HBox columnBox;
	
	public PseudoTable() {
		super();
		initialize();
	}
	
	public PseudoTable(Collection<PseudoTableRow<T>> rows, Collection<T> items) {
		this();
		if (rows != null)
			rows.addAll(rows);
		if (items != null)
			items.addAll(items);
	}
	
	public PseudoTable(List<T> items) {
		this(null, items);
	}
	
	public PseudoTable(Collection<PseudoTableRow<T>> rows) {
		this(rows, null);
	}
	
	@Override
	public String getUserAgentStylesheet() {
		return CSS_FILE;
	}
	
	public PseudoTableRow<T> getFirstRow() {
		return firstRow;
	}
	
	public ObservableList<PseudoTableRow<T>> getRows() {
		return rows;
	}
	
	public PseudoTableRow<T> getLastRow() {
		return lastRow;
	}
	
	public ObservableList<T> getItems() {
		return items;
	}
	
	public final ObjectProperty<SelectionType> selectionTypeProperty() {
		return this.selectionType;
	}
	
	public final SelectionType getSelectionType() {
		return this.selectionTypeProperty().get();
	}
	
	public final void setSelectionType(final SelectionType selectionType) {
		if (selectionType == null)
			this.selectionTypeProperty().set(SelectionType.NONE);
		else
			this.selectionTypeProperty().set(selectionType);
	}
	
	public final ObjectProperty<T> selectionItemProperty() {
		return this.selectionItem;
	}
	
	public final T getSelectionItem() {
		return this.selectionItemProperty().get();
	}
	
	public final void setSelectionItem(final T selectionItem) {
		this.selectionItem.set(selectionItem);
	}
	
	public final ObjectProperty<PseudoTableRow<T>> selectionRowProperty() {
		return this.selectionRow;
	}
	
	public final PseudoTableRow<T> getSelectionRow() {
		return this.selectionRowProperty().get();
	}
	
	public final void setSelectionRow(final PseudoTableRow<T> selectionRow) {
		this.selectionRow.set(selectionRow);
	}
	
	public final ObjectProperty<PseudoTableCell<T>> selectionCellProperty() {
		return this.selectionCell;
	}
	
	public final PseudoTableCell<T> getSelectionCell() {
		return this.selectionCellProperty().get();
	}
	
	public final void setSelectionCell(final PseudoTableCell<T> selectionCell) {
		this.selectionCell.set(selectionCell);
	}
	
	public Consumer<T> getItemSelectedHandler() {
		return itemSelectedHandler;
	}
	
	public void setItemSelectedHandler(Consumer<T> itemSelectedHandler) {
		if (itemSelectedHandler == null)
			itemSelectedHandler = item -> {};
		this.itemSelectedHandler = itemSelectedHandler;
	}
	
	public Consumer<PseudoTableRow<T>> getRowSelectedHandler() {
		return rowSelectedHandler;
	}
	
	public void setRowSelectedHandler(Consumer<PseudoTableRow<T>> rowSelectedHandler) {
		if (rowSelectedHandler == null)
			rowSelectedHandler = row -> {};
		this.rowSelectedHandler = rowSelectedHandler;
	}
	
	public Consumer<PseudoTableCell<T>> getCellSelectedHandler() {
		return cellSelectedHandler;
	}
	
	public void setCellSelectedHandler(Consumer<PseudoTableCell<T>> cellSelectedHandler) {
		if (cellSelectedHandler == null)
			cellSelectedHandler = cell -> {};
		this.cellSelectedHandler = cellSelectedHandler;
	}
	
	public void triggerRefresh() {
		onRowVisibilityChanges();
		onContentChanges(columnBox.layoutBoundsProperty(), null, columnBox.getLayoutBounds());
	}
	
	public void clearSelection() {
		setSelectionItem(null);
		setSelectionRow(null);
		setSelectionCell(null);
		handleSelectionChanges(null, null, null);
	}
	
	public Optional<PseudoTableCell<T>> getCell(PseudoTableRow<T> row, T item) {
		List<PseudoTableRow<T>> list = new ArrayList<>(rows);
		list.addAll(Arrays.asList(firstRow, lastRow));
		return list.stream().filter(r -> r == row).findFirst().map(r -> r.findCell(item)).orElse(Optional.empty());
	}
	
	private void initialize() {
		buildUI();
		setupListener();
	}
	
	private void buildUI() {
		
		getStyleClass().add(SC_PSEUDO_TABLE);
		
		rowBox = new VBox(0.0);
		rowBox.setMinWidth(100.0);
		rowBox.getStyleClass().addAll(SC_FIRST_COLUMN, SC_CELL_TEXT);
		BorderPane.setMargin(rowBox, new Insets(0.0, 0.0, 2.0, 0.0));
		
		rowContainer = new StackPane();
		VBox.setVgrow(rowContainer, Priority.ALWAYS);
		
		rowScrollBar = new ScrollBar();
		rowScrollBar.setOrientation(Orientation.VERTICAL);
		rowScrollBar.setMaxHeight(Double.MAX_VALUE);
		StackPane.setAlignment(rowScrollBar, Pos.CENTER_LEFT);
		
		rowScrollPane = new ScrollPane();
		rowScrollPane.getStyleClass().addAll(SC_SCROLL_PANE);
		rowScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		rowScrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
		rowScrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		rowScrollPane.setFitToWidth(true);
		
		// remove default style class .scroll-pane to prevent problems
		rowScrollPane.getStyleClass().remove(SC_DEFAULT_SCROLL_PANE);
		// Optional.ofNullable(rowScrollPane.lookup(".viewport")).ifPresent(vp -> vp.setStyle(CSS_STYLE_SPECIAL_VP));
		
		rowContent = new VBox(0.0);
		rowContent.setAlignment(Pos.TOP_LEFT);
		rowContent.setMaxHeight(Double.MAX_VALUE);
		
		rowScrollPane.setContent(rowContent);
		rowContainer.getChildren().addAll(rowScrollBar, rowScrollPane);
		
		firstRow.setText("Name"); // XXX default value
		firstRow.setTable(this);
		final Label firstRowLabel = createLabel(firstRow, SC_FIRST_ROW, 0, null);
		firstRowLabel.setOnMouseClicked(this::handleSelection);
		firstRowLabel.setOnTouchReleased(this::handleSelection);
		
		lastRow.setTable(this);
		final Label lastRowLabel = createLabel(lastRow, SC_LAST_ROW, null, Bindings.createIntegerBinding(() -> rows.size() + 1, rows));
		lastRowLabel.setOnMouseClicked(this::handleSelection);
		lastRowLabel.setOnTouchReleased(this::handleSelection);
		
		rowBox.getChildren().addAll(firstRowLabel, rowContainer, lastRowLabel);
		
		columnScrollPane = new ScrollPane();
		columnScrollPane.getStyleClass().addAll(SC_SCROLL_PANE, SC_COLUMN_SCROLL_CONTAINER);
		columnScrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		columnScrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
		columnScrollPane.setFitToWidth(true);
		columnScrollPane.setFitToHeight(true);
		columnScrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		HBox.setHgrow(columnScrollPane, Priority.ALWAYS);
		
		columnBox = new HBox(0.0);
		columnBox.getStyleClass().addAll(SC_COLUMN_BOX);
		columnBox.setAlignment(Pos.TOP_LEFT);
		columnBox.setMaxHeight(Double.MAX_VALUE);
		
		columnScrollPane.setContent(columnBox);
		columnScrollPane.setTranslateX(-1.0);
		columnScrollPane.setTranslateY(-1.0);
		
		setLeft(rowBox);
		setCenter(columnScrollPane);
	}
	
	private void setupListener() {
		
		rowScrollPane.layoutBoundsProperty().addListener(this::handleRowScrollLayoutChanges);
		rowContent.layoutBoundsProperty().addListener(this::handleRowScrollLayoutChanges);
		
		triggerRefresh();
		columnBox.layoutBoundsProperty().addListener(this::onContentChanges);
		// columnScrollPane.layoutBoundsProperty().addListener(this::onContentChanges);
		
		rows.addListener(this::handleRowChanges);
		items.addListener(this::handleItemChanges);
		
		rowScrollBar.toFront();
		
		rowScrollBar.minProperty().bindBidirectional(rowScrollPane.vminProperty());
		rowScrollBar.maxProperty().bindBidirectional(rowScrollPane.vmaxProperty());
		rowScrollBar.valueProperty().bindBidirectional(rowScrollPane.vvalueProperty());
		
		handleSelectTypeChanges(selectionType, null, selectionType.get());
		selectionType.addListener(this::handleSelectTypeChanges);
		
		selectionItem.addListener((obs, o, n) -> handleSelectionChanges(n, null, null));
		selectionRow.addListener((obs, o, n) -> handleSelectionChanges(null, n, null));
		selectionCell.addListener((obs, o, n) -> handleSelectionChanges(null, null, n));
	}
	
	private void handleRowScrollLayoutChanges(ObservableValue<? extends Bounds> obs, Bounds o, Bounds n) {
		
		ScrollBar sb = (ScrollBar) rowScrollPane.lookup(CSS_SELECTOR_SCROLLBAR_VERTICAL);
		if (sb != null && !rowScrollBar.visibleAmountProperty().isBound()) {
			rowScrollBar.visibleAmountProperty().unbind();
			rowScrollBar.visibleAmountProperty().bindBidirectional(sb.visibleAmountProperty());
		}
		
		rowScrollBar.setVisible(rowContent.getLayoutBounds().getHeight() >= rowScrollPane.getLayoutBounds().getHeight());
		
		final String style = rowScrollBar.isVisible() ? "" : "-fx-padding: 0 0 0 0;";
		firstRow.setStyle(style);
		rowContent.getChildren().forEach(node -> node.setStyle(style));
		lastRow.setStyle(style);
	}
	
	private void onContentChanges(ObservableValue<? extends Bounds> obs, Bounds o, Bounds n) {
		getRefelctionFieldObject(ScrollBar.class, "hsb", columnScrollPane.getSkin())
				.ifPresent(sb -> lastRow.setStyle(sb.isVisible() ? CSS_STYLE_SPECIAL_LAST_ROW : ""));
	}
	
	@SuppressWarnings("unchecked")
	private <C> Optional<C> getRefelctionFieldObject(Class<C> clazz, String field, Object on) {
		if (on == null || field == null || field.isEmpty())
			return Optional.empty();
		Field f;
		try {
			f = on.getClass().getDeclaredField(field);
		} catch (NoSuchFieldException | SecurityException e) {
			return Optional.empty();
		}
		boolean a = f.isAccessible();
		f.setAccessible(true);
		Object o;
		try {
			o = f.get(on);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			return Optional.empty();
		} finally {
			f.setAccessible(a);
		}
		return (Optional<C>) Optional.ofNullable(o);
	}
	
	private void handleSelectTypeChanges(ObservableValue<? extends SelectionType> obs, SelectionType o, SelectionType n) {
		// TODO reset everything on change!
		if (o == null)
			return;
		handleTypeAction(o, () -> updateSelectionColumn(null), () -> updateSelectionRow(null), () -> updateSelectionCell(null),
				() -> setSelectionRow(null));
	}
	
	private void handleSelectionChanges(T item, PseudoTableRow<T> row, PseudoTableCell<T> cell) {
		SelectionType type = this.getSelectionType();
		handleTypeAction(type, () -> {
			if (item != null) {
				updateSelectionColumn(item);
			}
		}, () -> {
			if (row != null) {
				updateSelectionRow(row);
			}
		}, () -> {
			if (cell != null) {
				updateSelectionCell(cell);
			}
		}, null);
	}
	
	private void updateSelectionColumn(T item) {
		// FIXME can be improved by remembering the old node -> weak reference?
		for (Node node : columnBox.getChildren()) {
			Object obj = node.getProperties().get(PROP_ITEM);
			if (obj == null)
				continue;
			node.pseudoClassStateChanged(selectedClass, obj == item);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void updateSelectionRow(PseudoTableRow<T> row) {
		// FIXME can be improved by remembering the old rows labels -> weak references?
		firstRow.pseudoClassStateChanged(selectedClass, firstRow == row);
		rows.forEach(r -> r.pseudoClassStateChanged(selectedClass, r == row));
		lastRow.pseudoClassStateChanged(selectedClass, lastRow == row);
		updateSelection(PseudoTableCell.class, selectedClass, node -> ((PseudoTableCell<T>) node).getRow() == row);
	}
	
	private void updateSelectionCell(PseudoTableCell<T> cell) {
		// FIXME can be improved by remembering the old cell -> weak reference?
		updateSelection(PseudoTableCell.class, selectedCellClass, node -> node == cell);
	}
	
	private <C> void updateSelection(Class<C> clazz, PseudoClass pseudoClass, Function<Node, Boolean> predicate) {
		for (Node node : columnBox.getChildrenUnmodifiable()) {
			Node first = node.lookup("." + SC_FIRST_ROW);
			if (first != null && clazz.isInstance(first)) {
				first.pseudoClassStateChanged(pseudoClass, predicate.apply(first));
			}
			Node last = node.lookup("." + SC_LAST_ROW);
			if (last != null && clazz.isInstance(last)) {
				last.pseudoClassStateChanged(pseudoClass, predicate.apply(last));
			}
			Node rowBox = node.lookup("." + SC_COLUMN_ROW_BOX);
			if (rowBox != null && rowBox instanceof VBox) {
				((VBox) rowBox).getChildrenUnmodifiable().forEach(n -> {
					if (clazz.isInstance(n)) {
						n.pseudoClassStateChanged(pseudoClass, predicate.apply(n));
					}
				});
			}
		}
	}
	
	private void onRowVisibilityChanges() {
		PseudoTableRow<T> firstVisible = null;
		PseudoTableRow<T> lastVisible = null;
		for (PseudoTableRow<T> row : rows) {
			if (row.isVisible() && firstVisible == null)
				firstVisible = row;
			if (row.isVisible())
				lastVisible = row;
		}
		for (PseudoTableRow<T> row : rows) {
			row.getStyleClass().removeAll(SC_FIRST_ROW, SC_LAST_ROW);
			for (T item : items) {
				row.findCell(item).ifPresent(cell -> cell.getStyleClass().removeAll(SC_FIRST_ROW, SC_LAST_ROW));
			}
			List<String> sc = new ArrayList<>();
			if (row.isVisible()) {
				if (row == firstVisible)
					sc.add(SC_FIRST_ROW);
				if (row == lastVisible)
					sc.add(SC_LAST_ROW);
			}
			if (!sc.isEmpty()) {
				row.getStyleClass().addAll(sc);
				for (T item : items) {
					row.findCell(item).ifPresent(cell -> cell.getStyleClass().addAll(sc));
				}
			}
		}
	}
	
	void handleRowChanges(Change<? extends PseudoTableRow<T>> change) {
		while (change.next()) {
			List<Node> toDelete = new ArrayList<>();
			for (PseudoTableRow<T> row : change.getRemoved()) {
				for (Node node : rowContent.getChildren()) {
					if (node == row) {
						toDelete.add(node);
						break;
					}
				}
			}
			rowContent.getChildren().removeAll(toDelete);
			if (items.size() > 0) {
				for (PseudoTableRow<T> row : change.getRemoved()) {
					columnBox.getChildrenUnmodifiable().forEach(n -> updateColumns(row, n, rows.indexOf(row), false));
				}
			}
			buildRows(change.getAddedSubList());
		}
		for (int i = 0; i < rows.size(); i++) {
			PseudoTableRow<T> row = rows.get(i);
			row.getProperties().put(PROP_INDEX, i + 1);
		}
		triggerRefresh();
	}
	
	private void buildRows(List<? extends PseudoTableRow<T>> rows) {
		for (PseudoTableRow<T> row : rows) {
			final Label label = createRow(row);
			if (!rowContent.getChildren().contains(label)) {
				row.setTable(this);
				row.visibleProperty().addListener((obs, o, n) -> onRowVisibilityChanges());
				rowContent.getChildren().add(label);
				if (items.size() > 0)
					columnBox.getChildrenUnmodifiable().forEach(n -> updateColumns(row, n, rows.indexOf(row), true));
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void updateColumns(PseudoTableRow<T> row, Node column, int index, boolean add) {
		T item = (T) column.getProperties().get(PROP_ITEM);
		if (item == null)
			return;
		if (add) {
			VBox rowBox = (VBox) column.lookup("." + SC_COLUMN_ROW_BOX);
			if (rowBox == null)
				return;
			createCell(row, item, index, null).ifPresent(cell -> rowBox.getChildren().add(index, cell));
		} else {
			row.findCell(item).ifPresent(cell -> ((VBox) cell.getParent()).getChildren().remove(cell));
		}
	}
	
	private Label createRow(PseudoTableRow<T> row) {
		int index = rows.indexOf(row);
		final Label label = createLabel(row, null, index + 1, null);
		label.setOnMouseClicked(this::handleSelection);
		label.setOnTouchReleased(this::handleSelection);
		return label;
	}
	
	@SuppressWarnings("unchecked")
	private void handleSelection(Event event) {
		
		if (event == null || event.getSource() == null)
			return;
		
		PseudoTableRow<T> row = (PseudoTableRow<T>) event.getSource();
		SelectionType type = this.getSelectionType();
		
		int count = 0;
		boolean mouse = false;
		MouseButton button = null;
		if (event instanceof MouseEvent) {
			mouse = true;
			count = ((MouseEvent) event).getClickCount();
			button = ((MouseEvent) event).getButton();
		}
		
		this.setSelectionItem(null);
		this.setSelectionCell(null);
		
		Runnable r = () -> this.setSelectionRow(row);
		handleTypeAction(type, r, r, r, () -> setSelectionRow(null));
		
		handleRowSelection(row, mouse, button, count);
		event.consume();
	}
	
	protected void handleRowSelection(PseudoTableRow<T> row, boolean mouse, MouseButton button, int count) {
		// special internal handler - override, if required
		SelectionType type = this.getSelectionType();
		if (SelectionType.ROW == type && row != null) {
			if (!mouse || MouseButton.PRIMARY == button)
				rowSelectedHandler.accept(row);
		}
		
		if (mouse) {
			// TODO allow separate method for this kind of events
			// IDEA: extend PseudoTableRow to allow selection Events
		}
	}
	
	void handleItemChanges(Change<? extends T> change) {
		while (change.next()) {
			List<Node> toDelete = new ArrayList<>();
			for (T item : change.getRemoved()) {
				for (Node node : columnBox.getChildren()) {
					Object obj = node.getProperties().get(PROP_ITEM);
					if (obj == item) {
						toDelete.add(node);
						break;
					}
				}
				// keep the rows cell mapping in sync
				rows.forEach(row -> row.removeCell(item));
			}
			columnBox.getChildren().removeAll(toDelete);
			buildColumns(change.getAddedSubList());
		}
		triggerRefresh();
	}
	
	private void buildColumns(List<? extends T> items) {
		for (T item : items) {
			final Pane pane = createColumn(item);
			if (!columnBox.getChildren().contains(pane)) {
				columnBox.getChildren().add(pane);
			}
		}
	}
	
	private Pane createColumn(T item) {
		
		final VBox pane = new VBox();
		pane.getStyleClass().addAll(SC_COLUMN_CONTAINER, SC_COLUMN, SC_CELL_TEXT);
		pane.setMinWidth(100.0);
		pane.setMaxWidth(150.0);
		
		pane.getProperties().put(PROP_ITEM, item);
		
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.getStyleClass().addAll(SC_SCROLL_PANE);
		scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);
		scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		VBox.setVgrow(scrollPane, Priority.ALWAYS);
		
		// I have now clue why, but this is necessary
		// scrollPane.setStyle(CSS_STYLE_SPECIAL_SP);
		// with removing the default class ".scroll-pane", the issue is gone
		scrollPane.getStyleClass().remove(SC_DEFAULT_SCROLL_PANE);
		
		scrollPane.vminProperty().bindBidirectional(rowScrollBar.minProperty());
		scrollPane.vmaxProperty().bindBidirectional(rowScrollBar.maxProperty());
		scrollPane.vvalueProperty().bindBidirectional(rowScrollBar.valueProperty());
		
		VBox rowBox = new VBox();
		rowBox.getStyleClass().add(SC_COLUMN_ROW_BOX);
		rowBox.setMaxWidth(Double.MAX_VALUE);
		
		for (int i = 0; i < rows.size(); i++) {
			createCell(rows.get(i), item, i, null).ifPresent(rowBox.getChildren()::add);
		}
		
		scrollPane.setContent(rowBox);
		
		createCell(firstRow, item, 0, null).ifPresent(pane.getChildren()::add);
		pane.getChildren().add(scrollPane);
		createCell(lastRow, item, rows.size(), Bindings.createIntegerBinding(() -> rows.size() + 1, rows)).ifPresent(pane.getChildren()::add);
		
		pane.setOnMouseClicked(this::handleEmptySelection);
		pane.setOnTouchReleased(this::handleEmptySelection);
		
		return pane;
	}
	
	private Optional<PseudoTableCell<T>> createCell(PseudoTableRow<T> row, T item, int index, IntegerBinding indexBinding) {
		Optional<PseudoTableCell<T>> cell = row.getCell(item);
		if (!cell.isPresent())
			return Optional.empty();
		String styleClass = null;
		if (index == 0)
			styleClass = SC_FIRST_ROW;
		if (index == rows.size() - 1 || index == rows.size())
			styleClass = SC_LAST_ROW;
		createLabel(cell.get(), styleClass, index + 1, indexBinding);
		return cell;
	}
	
	private Label createLabel(Label label, String styleClass, Integer index, IntegerBinding indexBinding) {
		if (styleClass != null && !styleClass.isEmpty()) {
			label.getStyleClass().addAll(styleClass);
		}
		if (index != null) {
			label.getProperties().put(PROP_INDEX, index);
		}
		if (indexBinding != null) {
			label.getProperties().put(PROP_INDEX_BINDING, indexBinding);
			indexBinding.addListener((obs, o, n) -> label.getProperties().put(PROP_INDEX, n));
		}
		label.setAlignment(Pos.CENTER);
		label.setMaxWidth(Double.MAX_VALUE);
		label.setPadding(new Insets(0.0, 5.0, 0.0, 5.0));
		VBox.setVgrow(label, Priority.ALWAYS);
		return label;
	}
	
	@SuppressWarnings("unchecked")
	protected void handleEmptySelection(Event event) {
		if (event == null || ((VBox) event.getSource()).getProperties().get(PROP_ITEM) == null)
			return;
		
		int count = 0;
		boolean mouse = false;
		MouseButton button = null;
		if (event instanceof MouseEvent) {
			mouse = true;
			count = ((MouseEvent) event).getClickCount();
			button = ((MouseEvent) event).getButton();
		}
		
		SelectionType type = getSelectionType();
		T item = (T) ((VBox) event.getSource()).getProperties().get(PROP_ITEM);
		
		setSelectionRow(null);
		setSelectionCell(null);
		
		Runnable r = () -> {
			setSelectionItem(item);
			if (item != null)
				getItemSelectedHandler().accept(item);
		};
		handleTypeAction(type, r, r, r, () -> setSelectionItem(null));
		
		handleEmptySelection(item, mouse, button, count);
		event.consume();
	}
	
	protected void handleEmptySelection(T item, boolean mouse, MouseButton button, int count) {
		// override, if required
	}
	
	private void handleTypeAction(SelectionType type, Runnable onColumn, Runnable onRow, Runnable onCell, Runnable onNone) {
		switch (type) {
		case COLUMN:
			if (onColumn != null)
				onColumn.run();
			break;
		case ROW:
			if (onRow != null)
				onRow.run();
			break;
		case CELL:
			if (onCell != null)
				onCell.run();
			break;
		case NONE:
		default:
			if (onNone != null)
				onNone.run();
			break;
		}
	}
}
