package eu.dzim.tests.fx.controls;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class StackedHBox extends HBox {
	
	private static final Color[] DEFAULT_COLORS = new Color[] { Color.AQUA, Color.AZURE, Color.YELLOW, Color.LIGHTYELLOW, Color.YELLOWGREEN,
			Color.GREENYELLOW, Color.GREEN, Color.DARKGREEN, Color.ORANGERED, Color.RED };
	
	private ObservableList<Entry> entries = FXCollections.observableArrayList();
	private ObservableList<HBox> panes = FXCollections.observableArrayList();
	
	private ListChangeListener<Entry> entriesChangeListener = this::entriesChanged;
	private ListChangeListener<HBox> panesChangeListener = this::panesChanged;
	
	public StackedHBox() {
		entries.addListener(entriesChangeListener);
		panes.addListener(panesChangeListener);
	}
	
	public ObservableList<Entry> getEntries() {
		return entries;
	}
	
	private void entriesChanged(ListChangeListener.Change<? extends Entry> change) {
		while (change.next()) {
			for (Entry entry : change.getRemoved())
				removeEntry(entry);
			for (Entry entry : change.getAddedSubList())
				addEntry(entry);
		}
	}
	
	private void removeEntry(Entry entry) {
		for (HBox pane : panes) {
			if (pane.getUserData() != entry)
				continue;
			if (entry.customListener != null) {
				entries.removeListener(entry.customListener);
				entry.customListener = null;
			}
			if (entry.colorListener != null) {
				entry.colorListener = null;
			}
			panes.remove(pane);
		}
	}
	
	private void addEntry(final Entry entry) {
		final HBox box = new HBox();
		box.setUserData(entry);
		ListChangeListener<Entry> listener = (ListChangeListener<Entry>) change -> {
			handleListChange(entry, box);
		};
		entry.customListener = listener;
		entries.addListener(listener);
		handleListChange(entry, box);
		
		ChangeListener<Color> colorListener = (obs, o, n) -> {
			BackgroundFill fill = new BackgroundFill(getColor(entry), CornerRadii.EMPTY, new Insets(0.0, 0.0, 0.0, 0.0));
			Background bg = new Background(fill);
			box.setBackground(bg);
		};
		entry.colorListener = colorListener;
		colorListener.changed(entry.color, null, entry.getColor());
		
		Label l = new Label();
		l.textProperty().bind(entry.text);
		box.getChildren().add(l);
		
		panes.add(box);
	}
	
	private void handleListChange(final Entry entry, final HBox box) {
		List<DoubleProperty> values = new ArrayList<>();
		for (Entry e : entries) {
			values.add(e.value);
		}
		List<Observable> bindings = new ArrayList<>();
		bindings.add(widthProperty());
		bindings.addAll(values);
		DoubleBinding widthBinding = Bindings.createDoubleBinding(() -> {
			int sum = 0;
			for (Entry e : entries)
				sum += e.value.get();
			final double percentage;
			if (sum == 0)
				percentage = 0.0;
			else
				percentage = entry.value.get() / sum;
			return getWidth() * percentage;
		}, bindings.toArray(new Observable[values.size()]));
		box.minWidthProperty().bind(widthBinding);
		box.maxWidthProperty().bind(widthBinding);
	}
	
	private Color getColor(Entry entry) {
		int index = entries.indexOf(entry);
		Color color = entry.color.get() == null ? DEFAULT_COLORS[index % DEFAULT_COLORS.length] : entry.color.get();
		return color;
	}
	
	private void panesChanged(ListChangeListener.Change<? extends HBox> change) {
		while (change.next()) {
			for (HBox pane : change.getRemoved())
				getChildren().remove(pane);
			for (HBox pane : change.getAddedSubList())
				getChildren().add(pane);
		}
	}
	
	public static class Entry {
		
		private DoubleProperty value = new SimpleDoubleProperty(0.0);
		private ObjectProperty<Color> color = new SimpleObjectProperty<>(null);
		private StringProperty text = new SimpleStringProperty(null);
		
		private ListChangeListener<Entry> customListener = null;
		private ChangeListener<Color> colorListener = null;
		
		public Entry(double value) {
			this.value.set(value);
		}
		
		public Entry(double value, Color color) {
			this(value);
			this.color.set(color);
		}
		
		public Entry(double value, Color color, String text) {
			this(value, color);
			this.text.set(text);
		}
		
		public final DoubleProperty valueProperty() {
			return this.value;
		}
		
		public final double getValue() {
			return this.valueProperty().get();
		}
		
		public final void setValue(final double value) {
			this.valueProperty().set(value);
		}
		
		public final ObjectProperty<Color> colorProperty() {
			return this.color;
		}
		
		public final Color getColor() {
			return this.colorProperty().get();
		}
		
		public final void setColor(final Color color) {
			this.colorProperty().set(color);
		}
		
		public final StringProperty textProperty() {
			return this.text;
		}
		
		public final String getText() {
			return this.textProperty().get();
		}
		
		public final void setText(final String text) {
			this.textProperty().set(text);
		}
		
		public ListChangeListener<Entry> getCustomListener() {
			return customListener;
		}
		
		public void setCustomListener(ListChangeListener<Entry> customListener) {
			this.customListener = customListener;
		}
		
		public ChangeListener<Color> getColorListener() {
			return colorListener;
		}
		
		public void setColorListener(ChangeListener<Color> colorListener) {
			this.colorListener = colorListener;
		}
	}
}
