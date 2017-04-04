package eu.dzim.shared.fx.ui;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.dzim.shared.disposable.Disposable;
import eu.dzim.shared.fx.fxml.FXMLLoaderService;
import eu.dzim.shared.fx.util.UIComponentType;
import eu.dzim.shared.util.SingleAcceptor;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

public class SwipePanePlaceholder<T> extends AnchorPane {
	
	private static final Logger LOG = LogManager.getLogger(SwipePanePlaceholder.class);
	
	private final FXMLLoaderService fxmlLoaderService;
	
	private final ObjectProperty<UIComponentType> component = new SimpleObjectProperty<>(this, "content", null);
	private final BooleanProperty show = new SimpleBooleanProperty(this, "show", false);
	private final BooleanProperty keep = new SimpleBooleanProperty(this, "keep", false);
	private final DoubleProperty scrollRestriction = new SimpleDoubleProperty(this, "scrollRestriction", 0.0);
	
	private final ObjectProperty<T> data = new SimpleObjectProperty<>(this, "data", null);
	
	private Pane content = null;
	private Object controller = null;
	
	public SwipePanePlaceholder() {
		this(null, null, false);
	}
	
	public SwipePanePlaceholder(FXMLLoaderService fxmlLoaderService) {
		this(fxmlLoaderService, null, false);
	}
	
	public SwipePanePlaceholder(FXMLLoaderService fxmlLoaderService, UIComponentType component) {
		this(fxmlLoaderService, component, false);
	}
	
	public SwipePanePlaceholder(FXMLLoaderService fxmlLoaderService, UIComponentType component, boolean keep) {
		this(fxmlLoaderService, component, keep, 1.0);
	}
	
	public SwipePanePlaceholder(FXMLLoaderService fxmlLoaderService, UIComponentType component, boolean keep, double scrollRestriction) {
		this.fxmlLoaderService = fxmlLoaderService;
		this.component.set(component);
		this.keep.set(keep);
		this.scrollRestriction.set(scrollRestriction);
		this.init();
	}
	
	public final ObjectProperty<UIComponentType> componentProperty() {
		return this.component;
	}
	
	public final UIComponentType getComponent() {
		return this.componentProperty().get();
	}
	
	public final void setComponent(final UIComponentType component) {
		this.componentProperty().set(component);
	}
	
	public final BooleanProperty showProperty() {
		return this.show;
	}
	
	public final boolean isShow() {
		return this.showProperty().get();
	}
	
	public final void setShow(final boolean show) {
		this.showProperty().set(show);
	}
	
	public final BooleanProperty keepProperty() {
		return this.keep;
	}
	
	public final boolean isKeep() {
		return this.keepProperty().get();
	}
	
	public final void setKeep(final boolean keep) {
		this.keepProperty().set(keep);
	}
	
	public final DoubleProperty scrollRestrictionProperty() {
		return this.scrollRestriction;
	}
	
	public final double getScrollRestriction() {
		return this.scrollRestrictionProperty().get();
	}
	
	public final void setScrollRestriction(final double scrollRestriction) {
		this.scrollRestrictionProperty().set(scrollRestriction);
	}
	
	public final ObjectProperty<T> dataProperty() {
		return this.data;
	}
	
	public final T getData() {
		return this.dataProperty().get();
	}
	
	public final void setData(final T data) {
		this.dataProperty().set(data);
	}
	
	public Pane getContent() {
		return content;
	}
	
	public Object getController() {
		return controller;
	}
	
	private void init() {
		show.addListener(this::handleShowChanges);
	}
	
	private void handleShowChanges(ObservableValue<? extends Boolean> obs, Boolean o, Boolean n) {
		
		// not decidable
		if (n == null)
			return;
		// we are requested to unload the UI, but it is not loaded at all
		if (!n && content == null)
			return;
		// we are requested to load the UI, but it is already loaded
		if (n && content != null)
			return;
		
		// load and show the UI
		if (n) {
			
			// no UIComponentType to present specified
			if (component.get() == null)
				return;
			
			Pair<Pane, Object> loaded = loadComponent(component.get());
			
			// something went wrong
			if (loaded == null)
				return;
			
			content = loaded.getKey();
			controller = loaded.getValue();
			
			AnchorPane.setTopAnchor(content, 0.0);
			AnchorPane.setRightAnchor(content, 0.0);
			AnchorPane.setBottomAnchor(content, 0.0);
			AnchorPane.setLeftAnchor(content, 0.0);
			
			getChildren().add(content);
			
		}
		// remove the UI
		else {
			
			// don't destroy the UI, if we explicitly want to keep it
			if (content != null && isKeep())
				return;
			
			// if the Disposable interface is implemented, trigger the orderly shutdown
			if (controller != null && controller instanceof Disposable)
				((Disposable) controller).dispose();
			
			getChildren().remove(content);
			
			content = null;
			controller = null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private Pair<Pane, Object> loadComponent(UIComponentType component) {
		try {
			FXMLLoader loader = fxmlLoaderService.getLoader(component);
			Pane content = loader.load();
			Object controller = loader.getController();
			if (controller instanceof SingleAcceptor) {
				((SingleAcceptor<T>) controller).accept(data.get());
			}
			return new Pair<Pane, Object>(content, controller);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
}