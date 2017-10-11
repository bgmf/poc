package eu.dzim.shared.fx.ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.transitions.CachedTransition;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import eu.dzim.shared.fx.util.ColorConstants;
import eu.dzim.shared.fx.util.SharedUIUtils;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.DefaultProperty;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

@DefaultProperty(value = "content")
public class SimpleDialog extends StackPane {
	
	public enum SizingStrategy {
		USE_PREF_SIZE, USE_MAX_WIDTH, USE_MAX_HEIGH, USE_MAX_SIZE;
	}
	
	public enum AnimationStrategy {
		SCALE_FADE, SCALE, FADE, NONE;
	}
	
	private static final Logger LOG = LogManager.getLogger(SimpleDialog.class);
	
	private static final ObservableMap<Stage, ObservableList<SimpleDialog>> OPEN_DIALOGS = FXCollections.observableHashMap();
	
	private static final Duration DEFAULT_ANIMATION_DURATION = Duration.millis(250);
	
	protected static final URL DIALOGS_CSS_URL = SimpleDialog.class.getResource("/ch/cnlab/aschwanden/ui/simple-dialogs.css");
	
	public static final DropShadow DEFAULT_EFFECT = new DropShadow(BlurType.GAUSSIAN, Color.rgb(0, 0, 0, 0.26), 25, 0.25, 0, 8);
	
	public static final String STYLE_CLASS = "simple-dialog-overlay-pane";
	public static final String CONTENT_HOLDER_STYLE_CLASS = "content-holder";
	public static final String DECORATION_STYLE_CLASS = "dialog-decoration";
	public static final String DECORATION_TOP_STYLE_CLASS = "toolbar";
	public static final String DECORATION_TITLE_STYLE_CLASS = "title";
	public static final String DECORATION_CLOSE_STYLE_CLASS = "close";
	public static final String DECORATION_CLOSE_ICON_STYLE_CLASS = "icon";
	public static final String DECORATION_CENTER_STYLE_CLASS = "content";
	public static final String DECORATION_RESIZE_STYLE_CLASS = "window-resize-corner";
	
	// for modal dialogs
	private final Object myLock = new Object();
	private Object result = null;
	
	private Stage myStage = null;
	
	private StackPane dialogContainer;
	private StackPane contentHolder;
	private BorderPane decoratedContentHolder;
	private Region content;
	private Transition animation;
	
	// XXX we could use this like the JFXDialog - translate the dialog in/out from/to a position
	@SuppressWarnings("unused") private double offsetX = 0;
	@SuppressWarnings("unused") private double offsetY = 0;
	
	private EventHandler<? super MouseEvent> closeHandlerMouse = (e) -> close();
	private EventHandler<? super TouchEvent> closeHandlerTouch = (e) -> close();
	
	private BooleanProperty modal = new SimpleBooleanProperty(false);
	private BooleanProperty overlayClose = new SimpleBooleanProperty(true);
	
	private BooleanProperty useDecoration = new SimpleBooleanProperty(false);
	private BooleanProperty showClose = new SimpleBooleanProperty(true);
	private BooleanProperty draggable = new SimpleBooleanProperty(false);
	private BooleanProperty resizableDialog = new SimpleBooleanProperty(false);
	private StringProperty title = new SimpleStringProperty("");
	private ObjectProperty<Node> titleGraphic = new SimpleObjectProperty<>(null);
	
	private ObjectProperty<SizingStrategy> sizingStrategy = new SimpleObjectProperty<>(SizingStrategy.USE_PREF_SIZE);
	private ObjectProperty<AnimationStrategy> animationStrategy = new SimpleObjectProperty<>(AnimationStrategy.NONE);
	private ObjectProperty<Duration> animationDuration = new SimpleObjectProperty<>(DEFAULT_ANIMATION_DURATION);
	private BooleanProperty useAnimationCaching = new SimpleBooleanProperty(true);
	
	private ObjectProperty<Pos> dialogPosition = new SimpleObjectProperty<>(Pos.CENTER);
	private ObjectProperty<Pos> decoratedDialogPosition = new SimpleObjectProperty<>(Pos.CENTER);
	
	private ObjectProperty<Color> dialogBackground = new SimpleObjectProperty<>(Color.WHITE);
	private ObjectProperty<Color> overlayBackground = new SimpleObjectProperty<>(Color.rgb(0, 0, 0, 0.7));
	
	private ObjectProperty<EventHandler<? super SimpleDialogEvent>> onDialogClosedProperty = new SimpleObjectProperty<>((closed) -> {
		if (dialogContainer != null)
			SharedUIUtils.startSleepThread(() -> dialogContainer.requestFocus());
	});
	private ObjectProperty<EventHandler<? super SimpleDialogEvent>> onDialogOpenedProperty = new SimpleObjectProperty<>((opened) -> {
		if (content != null)
			SharedUIUtils.startSleepThread(() -> content.requestFocus());
	});
	
	private ObjectProperty<EventHandler<? super SimpleDialogEvent>> onBeforeShowProperty = new SimpleObjectProperty<>((beforeShow) -> {});
	
	private Map<Node, Boolean> focusTraversable = new HashMap<>();
	
	public SimpleDialog() {
		this(null, null, AnimationStrategy.NONE);
	}
	
	public SimpleDialog(StackPane dialogContainer, Region content, AnimationStrategy animationStrategy) {
		this(dialogContainer, content, animationStrategy, true);
	}
	
	public SimpleDialog(StackPane dialogContainer, Region content, AnimationStrategy animationStrategy, boolean overlayClose) {
		initialize();
		setOverlayClose(overlayClose);
		setContent(content);
		setDialogContainer(dialogContainer, true);
		setAnimationStrategy(animationStrategy);
		initChangeListeners();
	}
	
	private void initChangeListeners() {
		overlayCloseProperty().addListener((o, oldVal, newVal) -> {
			if (newVal) {
				this.addEventHandler(MouseEvent.MOUSE_PRESSED, closeHandlerMouse);
				this.addEventHandler(TouchEvent.TOUCH_PRESSED, closeHandlerTouch);
			} else {
				this.removeEventHandler(MouseEvent.MOUSE_PRESSED, closeHandlerMouse);
				this.removeEventHandler(TouchEvent.TOUCH_PRESSED, closeHandlerTouch);
			}
		});
	}
	
	private void initialize() {
		this.setVisible(false);
		// this.getStyleClass().add(DEFAULT_STYLE_CLASS);
		this.animationStrategy.addListener((o, oldVal, newVal) -> {
			animation = getShowAnimation(animationStrategy.get());
		});
		
		contentHolder = new StackPane();
		contentHolder.getStyleClass().add(CONTENT_HOLDER_STYLE_CLASS);
		contentHolder.setBackground(new Background(new BackgroundFill(dialogBackground.get(), new CornerRadii(2), null)));
		dialogBackground.addListener(
				(obs, o, n) -> contentHolder.setBackground(new Background(new BackgroundFill(dialogBackground.get(), new CornerRadii(2), null))));
		contentHolder.setPickOnBounds(false);
		contentHolder.setCacheHint(CacheHint.QUALITY);
		
		updateSizingStrategy();
		sizingStrategy.addListener((obs, o, n) -> updateSizingStrategy());
		
		this.getChildren().add(contentHolder);
		this.getStyleClass().add(STYLE_CLASS);
		
		updateDialogPosition();
		dialogPosition.addListener((obs, o, n) -> updateDialogPosition());
		
		this.setBackground(new Background(new BackgroundFill(overlayBackground.get(), null, null)));
		overlayBackground.addListener((obs, o, n) -> this.setBackground(new Background(new BackgroundFill(overlayBackground.get(), null, null))));
		
		// close the dialog if clicked on the overlay pane
		if (overlayClose.get()) {
			this.addEventHandler(MouseEvent.MOUSE_PRESSED, closeHandlerMouse);
			this.addEventHandler(TouchEvent.TOUCH_PRESSED, closeHandlerTouch);
		}
		
		// prevent propagating the events to overlay pane
		contentHolder.addEventHandler(MouseEvent.ANY, (e) -> e.consume());
		contentHolder.addEventHandler(TouchEvent.ANY, (e) -> e.consume());
	}
	
	private void updateSizingStrategy() {
		switch (sizingStrategy.get() == null ? SizingStrategy.USE_PREF_SIZE : sizingStrategy.get()) {
		default:
			// intended fall-through
		case USE_PREF_SIZE:
			// we need to unbind it, otherwise, we can't update the max size or re-bind
			contentHolder.maxWidthProperty().unbind();
			contentHolder.maxHeightProperty().unbind();
			// use max size by default
			contentHolder.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
			// if the content node is set up...
			if (content != null) {
				// ... check whether the pref with is set (otherwise: -1 = use computed size)
				
				if (content.getPrefWidth() > 0)
					contentHolder.maxWidthProperty().bind(content.prefWidthProperty());
				// ... check whether the pref height is set (otherwise: -1 = use computed size)
				if (content.getPrefHeight() > 0) {
					if (isUseDecoration() && decoratedContentHolder != null) {
						contentHolder.maxHeightProperty()
								.bind(content.prefHeightProperty().add(((HBox) decoratedContentHolder.getTop()).heightProperty()));
					} else {
						contentHolder.maxHeightProperty().bind(content.prefHeightProperty());
					}
				}
				// we need to do the pref width/height checks, or we end up with full-screen dialogs
				// with tiny content
			}
			break;
		case USE_MAX_WIDTH:
			contentHolder.setMaxSize(Double.MAX_VALUE, Region.USE_PREF_SIZE);
			break;
		case USE_MAX_HEIGH:
			contentHolder.setMaxSize(Region.USE_PREF_SIZE, Double.MAX_VALUE);
			break;
		case USE_MAX_SIZE:
			contentHolder.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			break;
		}
	}
	
	private void updateDialogPosition() {
		StackPane.setAlignment(contentHolder, dialogPosition.get() == null ? Pos.CENTER : dialogPosition.get());
	}
	
	/**
	 * @return all open lightweight dialogs from all {@link Stage}s
	 */
	public static ObservableList<SimpleDialog> getOpenDialogs() {
		ObservableList<SimpleDialog> clone = FXCollections.observableArrayList();
		OPEN_DIALOGS.values().forEach(clone::addAll);
		return FXCollections.unmodifiableObservableList(clone);
	}
	
	/**
	 * @param stage
	 *            the {@link Stage} to query the open lightweight dialogs for
	 * @return all open lightweight dialogs from the specified {@link Stage}
	 */
	public static ObservableList<SimpleDialog> getOpenDialogs(Stage stage) {
		if (OPEN_DIALOGS.get(stage) == null)
			return FXCollections.unmodifiableObservableList(FXCollections.observableArrayList());
		return FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(OPEN_DIALOGS.get(stage)));
	}
	
	public final BooleanProperty modalProperty() {
		return this.modal;
	}
	
	public final boolean isModal() {
		return this.modalProperty().get();
	}
	
	public final void setModal(final boolean modal) {
		this.modalProperty().set(modal);
	}
	
	public <T> void setResult(T result) {
		this.result = result;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getResult() {
		if (result == null)
			return null;
		return (T) result;
	}
	
	public final BooleanProperty overlayCloseProperty() {
		return this.overlayClose;
	}
	
	public final boolean isOverlayClose() {
		return this.overlayCloseProperty().get();
	}
	
	public final void setOverlayClose(final boolean overlayClose) {
		this.overlayCloseProperty().set(overlayClose);
	}
	
	public final BooleanProperty useDecorationProperty() {
		return this.useDecoration;
	}
	
	public final boolean isUseDecoration() {
		return this.useDecorationProperty().get();
	}
	
	public final void setUseDecoration(final boolean useDecoration) {
		this.useDecorationProperty().set(useDecoration);
	}
	
	public final BooleanProperty showCloseProperty() {
		return this.showClose;
	}
	
	public final boolean isShowClose() {
		return this.showCloseProperty().get();
	}
	
	public final void setShowClose(final boolean showClose) {
		this.showCloseProperty().set(showClose);
	}
	
	public final BooleanProperty draggableProperty() {
		return this.draggable;
	}
	
	public final boolean isDraggable() {
		return this.draggableProperty().get();
	}
	
	public final void setDraggable(final boolean draggable) {
		this.draggableProperty().set(draggable);
	}
	
	public final BooleanProperty resizeableDialogProperty() {
		return this.resizableDialog;
	}
	
	public final boolean isResizableDialog() {
		return this.resizeableDialogProperty().get();
	}
	
	public final void setResizableDialog(final boolean resizableDialog) {
		this.resizeableDialogProperty().set(resizableDialog);
	}
	
	public final StringProperty titleProperty() {
		return this.title;
	}
	
	public final String getTitle() {
		return this.titleProperty().get();
	}
	
	public final void setTitle(final String title) {
		this.titleProperty().set(title);
	}
	
	public final ObjectProperty<Node> titleGraphicProperty() {
		return this.titleGraphic;
	}
	
	public final Node getTitleGraphic() {
		return this.titleGraphicProperty().get();
	}
	
	public final void setTitleGraphic(final Node titleGraphic) {
		this.titleGraphicProperty().set(titleGraphic);
	}
	
	public final ObjectProperty<SizingStrategy> sizingStrategyProperty() {
		return this.sizingStrategy;
	}
	
	public final SizingStrategy getSizingStrategy() {
		return this.sizingStrategyProperty().get();
	}
	
	public final void setSizingStrategy(final SizingStrategy sizingStrategy) {
		this.sizingStrategyProperty().set(sizingStrategy);
	}
	
	public final ObjectProperty<AnimationStrategy> animationStrategyProperty() {
		return this.animationStrategy;
	}
	
	public final AnimationStrategy getAnimationStrategy() {
		return this.animationStrategyProperty().get();
	}
	
	public final void setAnimationStrategy(final AnimationStrategy animationStrategy) {
		this.animationStrategyProperty().set(animationStrategy);
	}
	
	public final ObjectProperty<Duration> animationDurationProperty() {
		return this.animationDuration;
	}
	
	public final Duration getAnimationDuration() {
		return this.animationDurationProperty().get();
	}
	
	public final void setAnimationDuration(final Duration animationDuration) {
		this.animationDurationProperty().set(animationDuration);
	}
	
	public final BooleanProperty useAnimationCachingProperty() {
		return this.useAnimationCaching;
	}
	
	public final boolean isUseAnimationCaching() {
		return this.useAnimationCachingProperty().get();
	}
	
	public final void setUseAnimationCaching(final boolean useAnimationCaching) {
		this.useAnimationCachingProperty().set(useAnimationCaching);
	}
	
	public final ObjectProperty<Pos> dialogPositionProperty() {
		return this.dialogPosition;
	}
	
	public final Pos getDialogPosition() {
		return this.dialogPositionProperty().get();
	}
	
	public final void setDialogPosition(final Pos dialogPosition) {
		this.dialogPositionProperty().set(dialogPosition);
	}
	
	public final ObjectProperty<Pos> decoratedDialogPositionProperty() {
		return this.decoratedDialogPosition;
	}
	
	public final Pos getDecoratedDialogPosition() {
		return this.decoratedDialogPositionProperty().get();
	}
	
	public final void setDecoratedDialogPosition(final Pos decoratedDialogPosition) {
		this.decoratedDialogPositionProperty().set(decoratedDialogPosition);
	}
	
	public final ObjectProperty<Color> dialogBackgroundProperty() {
		return this.dialogBackground;
	}
	
	public final Color getDialogBackground() {
		return this.dialogBackgroundProperty().get();
	}
	
	public final void setDialogBackground(final Color dialogBackground) {
		this.dialogBackgroundProperty().set(dialogBackground);
	}
	
	public final ObjectProperty<Color> overlayBackgroundProperty() {
		return this.overlayBackground;
	}
	
	public final Color getOverlayBackground() {
		return this.overlayBackgroundProperty().get();
	}
	
	public final void setOverlayBackground(final Color overlayBackground) {
		this.overlayBackgroundProperty().set(overlayBackground);
	}
	
	public void setOnDialogClosed(EventHandler<? super SimpleDialogEvent> handler) {
		onDialogClosedProperty.set(handler);
	}
	
	public EventHandler<? super SimpleDialogEvent> getOnDialogClosed() {
		return onDialogClosedProperty.get();
	}
	
	public void setOnDialogOpened(EventHandler<? super SimpleDialogEvent> handler) {
		onDialogOpenedProperty.set(handler);
	}
	
	public EventHandler<? super SimpleDialogEvent> getOnDialogOpened() {
		return onDialogOpenedProperty.get();
	}
	
	public void setOnBeforeShow(EventHandler<? super SimpleDialogEvent> handler) {
		onBeforeShowProperty.set(handler);
	}
	
	public EventHandler<? super SimpleDialogEvent> getOnBeforeShow() {
		return onBeforeShowProperty.get();
	}
	
	public StackPane getDialogContainer() {
		return dialogContainer;
	}
	
	/**
	 * set the dialog container Note: the dialog container must be StackPane, its the container for the dialog to be shown in.
	 * 
	 * @param dialogContainer
	 */
	public void setDialogContainer(StackPane dialogContainer, boolean newAnimation) {
		if (dialogContainer != null) {
			this.dialogContainer = dialogContainer;
			Scene _scene = this.dialogContainer.getScene();
			if (_scene != null && _scene.getWindow() != null) {
				this.myStage = (Stage) _scene.getWindow();
				// install CSS
				String dialogsCssUrl = DIALOGS_CSS_URL.toExternalForm();
				if (!_scene.getStylesheets().contains(dialogsCssUrl)) {
					_scene.getStylesheets().addAll(dialogsCssUrl);
				}
			}
			if (this.dialogContainer.getChildren().indexOf(this) == -1
					|| this.dialogContainer.getChildren().indexOf(this) != this.dialogContainer.getChildren().size() - 1) {
				this.dialogContainer.getChildren().remove(this);
				this.dialogContainer.getChildren().add(this);
			}
			// FIXME: need to be improved to consider only the parent boundary
			offsetX = (this.getParent().getBoundsInLocal().getWidth());
			offsetY = (this.getParent().getBoundsInLocal().getHeight());
			if (newAnimation)
				animation = getShowAnimation(animationStrategy.get());
		}
	}
	
	/**
	 * @return dialog content node
	 */
	public Region getContent() {
		return content;
	}
	
	public StackPane getContentHolder() {
		return contentHolder;
	}
	
	/**
	 * set the content of the dialog
	 * 
	 * @param content
	 */
	public void setContent(Region content) {
		if (content != null) {
			content.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE); // XXX
			this.content = content;
			this.content.setPickOnBounds(false);
			updateSizingStrategy();
			if (dialogContainer == null) {
				contentHolder.getChildren().add(content);
			} else {
				setupDecoration();
				// setDialogContainer(dialogContainer, false);
				bindDecoratedDialogPosition();
			}
			
		}
	}
	
	/**
	 * it will show the dialog in the specified container
	 * 
	 * @param dialogContainer
	 */
	public <T> Optional<T> show(StackPane dialogContainer) {
		
		setupDecoration();
		setDialogContainer(dialogContainer, true);
		bindDecoratedDialogPosition();
		updateSizingStrategy();
		
		preventFocusTraversal();
		
		SimpleDialogEvent beforeShowEvent = new SimpleDialogEvent(this, contentHolder, SimpleDialogEvent.BEFORE_SHOW);
		onBeforeShowProperty.get().handle(beforeShowEvent);
		
		animation.play();
		addToOpenDialogs();
		
		if (isModal()) {
			// This forces the dialog to be modal
			// TODO with Java 9, this method is present in the Platform class
			try {
				Method m = Platform.class.getDeclaredMethod("enterNestedEventLoop", Object.class);
				setResult(m.invoke(null, myLock));
			} catch (NoSuchMethodException e) {
				// XXX assume Java 8
				setResult(com.sun.javafx.tk.Toolkit.getToolkit().enterNestedEventLoop(myLock));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				LOG.error(e.getMessage(), e);
			}
			return Optional.ofNullable(getResult());
		}
		return Optional.empty();
	}
	
	private void preventFocusTraversal() {
		dialogContainer.getChildren().forEach(node -> {
			if (node == this)
				return;
			// focusTraversable.put(node, node.isFocusTraversable());
			// node.setFocusTraversable(false);
			focusTraversable.put(node, node.isDisable());
			node.setDisable(true);
		});
	}
	
	private void resetFocusTraversal() {
		
		dialogContainer.getChildren().forEach(node -> {
			// Boolean b = focusTraversable.get(node);
			Boolean b = focusTraversable.get(node);
			if (b != null) {
				// node.setFocusTraversable(b);
				node.setDisable(b);
			}
		});
	}
	
	/**
	 * show the dialog inside its parent container
	 */
	public void show() {
		show(dialogContainer);
	}
	
	/**
	 * close the dialog
	 */
	public void close() {
		
		switch (animationStrategy.get() == null ? AnimationStrategy.NONE : animationStrategy.get()) {
		case SCALE_FADE:
			// intended fall-through
		case SCALE:
			// intended fall-through
		case FADE:
			animation.setRate(-1);
			animation.play();
			animation.setOnFinished((e) -> {
				closeAction();
			});
			break;
		case NONE:
			// intended fall-through
		default:
			closeAction();
			break;
		}
	}
	
	private void closeAction() {
		
		resetProperties();
		resetFocusTraversal();
		
		onDialogClosedProperty.get().handle(new SimpleDialogEvent(SimpleDialogEvent.CLOSED));
		dialogContainer.getChildren().remove(this);
		
		if (isModal()) {
			// This forces the dialog to be modal
			// TODO with Java 9, this method is present in the Platform class
			try {
				Method m = Platform.class.getDeclaredMethod("exitNestedEventLoop", Object.class, Object.class);
				m.invoke(null, myLock, result);
			} catch (NoSuchMethodException e) {
				// XXX assume Java 8
				com.sun.javafx.tk.Toolkit.getToolkit().exitNestedEventLoop(myLock, result);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		
		removeFromOpenDialogs();
	}
	
	private Transition getShowAnimation(AnimationStrategy animationStrategy) {
		Transition animation = null;
		if (contentHolder != null) {
			
			Duration duration = animationDuration.get() == null ? DEFAULT_ANIMATION_DURATION : animationDuration.get();
			switch (animationStrategy == null ? AnimationStrategy.NONE : animationStrategy) {
			case SCALE_FADE:
				// intended fall-through
			case SCALE:
				contentHolder.setScaleX(0);
				contentHolder.setScaleY(0);
				break;
			case FADE:
				contentHolder.setScaleX(1);
				contentHolder.setScaleY(1);
				break;
			case NONE:
				// intended fall-through
			default:
				contentHolder.setScaleX(1);
				contentHolder.setScaleY(1);
				duration = Duration.ZERO;
				break;
			}
			
			if (isUseAnimationCaching())
				animation = new CenterTransition(duration);
			else
				animation = new UncachedCenterTransition(duration);
		}
		if (animation != null)
			animation.setOnFinished((finish) -> onDialogOpenedProperty.get().handle(new SimpleDialogEvent(SimpleDialogEvent.OPENED)));
		return animation;
	}
	
	private void resetProperties() {
		
		this.setVisible(false);
		
		if (contentHolder.translateXProperty().isBound())
			contentHolder.translateXProperty().unbind();
		contentHolder.setTranslateX(0);
		
		if (contentHolder.translateYProperty().isBound())
			contentHolder.translateYProperty().unbind();
		contentHolder.setTranslateY(0);
		
		contentHolder.setScaleX(1);
		contentHolder.setScaleY(1);
	}
	
	private void addToOpenDialogs() {
		if (myStage == null)
			return;
		ObservableList<SimpleDialog> list = OPEN_DIALOGS.get(myStage);
		if (list == null) {
			list = FXCollections.observableArrayList();
			OPEN_DIALOGS.put(myStage, list);
		}
		list.add(this);
	}
	
	private void removeFromOpenDialogs() {
		if (myStage == null)
			return;
		ObservableList<SimpleDialog> list = OPEN_DIALOGS.get(myStage);
		if (list == null) {
			list = FXCollections.observableArrayList();
			OPEN_DIALOGS.put(myStage, list);
		}
		list.remove(this);
	}
	
	private void setupDecoration() {
		if (isUseDecoration()) {
			// setSizingStrategy(SizingStrategy.USE_PREF_SIZE);
			setDialogPosition(Pos.TOP_LEFT);
			contentHolder.getChildren().clear();
			decoratedContentHolder = buildDecoratedContainer(content);
			contentHolder.getChildren().add(decoratedContentHolder);
		} else {
			contentHolder.getChildren().clear();
			if (content != null)
				contentHolder.getChildren().add(content);
		}
	}
	
	private void bindDecoratedDialogPosition() {
		initDecoratedDialogPosition();
		decoratedDialogPositionProperty().addListener((obs, o, n) -> initDecoratedDialogPosition());
	}
	
	private void initDecoratedDialogPosition() {
		if (!isUseDecoration())
			return;
		if (contentHolder.translateXProperty().isBound())
			contentHolder.translateXProperty().unbind();
		if (contentHolder.translateYProperty().isBound())
			contentHolder.translateYProperty().unbind();
		switch (getDecoratedDialogPosition() == null ? Pos.CENTER : getDecoratedDialogPosition()) {
		case TOP_CENTER:
			contentHolder.translateXProperty()
					.bind(Bindings.createDoubleBinding(
							() -> dialogContainer.getLayoutBounds().getWidth() / 2.0 - contentHolder.getLayoutBounds().getWidth() / 2.0,
							dialogContainer.layoutBoundsProperty(), contentHolder.layoutBoundsProperty()));
			contentHolder.setTranslateY(0.0);
			break;
		case TOP_LEFT:
			contentHolder.setTranslateX(0.0);
			contentHolder.setTranslateY(0.0);
			break;
		case TOP_RIGHT:
			contentHolder.translateXProperty()
					.bind(Bindings.createDoubleBinding(
							() -> dialogContainer.getLayoutBounds().getWidth() - contentHolder.getLayoutBounds().getWidth(),
							dialogContainer.layoutBoundsProperty(), contentHolder.layoutBoundsProperty()));
			contentHolder.setTranslateY(0.0);
			break;
		case BASELINE_CENTER:
			// intended fall-through
		case BOTTOM_CENTER:
			contentHolder.translateXProperty()
					.bind(Bindings.createDoubleBinding(
							() -> dialogContainer.getLayoutBounds().getWidth() / 2.0 - contentHolder.getLayoutBounds().getWidth() / 2.0,
							dialogContainer.layoutBoundsProperty(), contentHolder.layoutBoundsProperty()));
			contentHolder.translateYProperty()
					.bind(Bindings.createDoubleBinding(
							() -> dialogContainer.getLayoutBounds().getHeight() - contentHolder.getLayoutBounds().getHeight(),
							dialogContainer.layoutBoundsProperty(), contentHolder.layoutBoundsProperty()));
			break;
		case BASELINE_LEFT:
			// intended fall-through
		case BOTTOM_LEFT:
			contentHolder.setTranslateX(0.0);
			contentHolder.translateYProperty()
					.bind(Bindings.createDoubleBinding(
							() -> dialogContainer.getLayoutBounds().getHeight() - contentHolder.getLayoutBounds().getHeight(),
							dialogContainer.layoutBoundsProperty(), contentHolder.layoutBoundsProperty()));
			break;
		case BASELINE_RIGHT:
			// intended fall-through
		case BOTTOM_RIGHT:
			contentHolder.translateXProperty()
					.bind(Bindings.createDoubleBinding(
							() -> dialogContainer.getLayoutBounds().getWidth() - contentHolder.getLayoutBounds().getWidth(),
							dialogContainer.layoutBoundsProperty(), contentHolder.layoutBoundsProperty()));
			contentHolder.translateYProperty()
					.bind(Bindings.createDoubleBinding(
							() -> dialogContainer.getLayoutBounds().getHeight() - contentHolder.getLayoutBounds().getHeight(),
							dialogContainer.layoutBoundsProperty(), contentHolder.layoutBoundsProperty()));
			break;
		case CENTER_LEFT:
			contentHolder.setTranslateX(0.0);
			contentHolder.translateYProperty()
					.bind(Bindings.createDoubleBinding(
							() -> dialogContainer.getLayoutBounds().getHeight() / 2.0 - contentHolder.getLayoutBounds().getHeight() / 2.0,
							dialogContainer.layoutBoundsProperty(), contentHolder.layoutBoundsProperty()));
			break;
		case CENTER_RIGHT:
			contentHolder.translateXProperty()
					.bind(Bindings.createDoubleBinding(
							() -> dialogContainer.getLayoutBounds().getWidth() - contentHolder.getLayoutBounds().getWidth(),
							dialogContainer.layoutBoundsProperty(), contentHolder.layoutBoundsProperty()));
			contentHolder.translateYProperty()
					.bind(Bindings.createDoubleBinding(
							() -> dialogContainer.getLayoutBounds().getHeight() / 2.0 - contentHolder.getLayoutBounds().getHeight() / 2.0,
							dialogContainer.layoutBoundsProperty(), contentHolder.layoutBoundsProperty()));
			break;
		case CENTER:
			// intended fall-through
		default:
			contentHolder.translateXProperty()
					.bind(Bindings.createDoubleBinding(
							() -> dialogContainer.getLayoutBounds().getWidth() / 2.0 - contentHolder.getLayoutBounds().getWidth() / 2.0,
							dialogContainer.layoutBoundsProperty(), contentHolder.layoutBoundsProperty()));
			contentHolder.translateYProperty()
					.bind(Bindings.createDoubleBinding(
							() -> dialogContainer.getLayoutBounds().getHeight() / 2.0 - contentHolder.getLayoutBounds().getHeight() / 2.0,
							dialogContainer.layoutBoundsProperty(), contentHolder.layoutBoundsProperty()));
			break;
		}
	}
	
	private BorderPane buildDecoratedContainer(Region content) {
		
		BorderPane pane = new BorderPane();
		pane.getStyleClass().add(DECORATION_STYLE_CLASS);
		
		Label title = new Label();
		title.getStyleClass().add(DECORATION_TITLE_STYLE_CLASS);
		title.setGraphicTextGap(5.0);
		title.setContentDisplay(ContentDisplay.LEFT);
		title.textProperty().bind(titleProperty());
		title.graphicProperty().bind(titleGraphicProperty());
		
		Region spacer = new Region();
		spacer.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(spacer, Priority.ALWAYS);
		
		Button close = new Button();
		close.getStyleClass().add(DECORATION_CLOSE_STYLE_CLASS);
		close.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		MaterialDesignIconView circle = new MaterialDesignIconView();
		circle.getStyleClass().add(DECORATION_CLOSE_ICON_STYLE_CLASS);
		circle.setGlyphName(MaterialDesignIcon.CLOSE_CIRCLE_OUTLINE.name());
		circle.setGlyphSize(20.0);
		circle.setFill(ColorConstants.WHITE);
		StackPane closePane = new StackPane(circle);
		close.setGraphic(closePane);
		close.setOnAction(action -> close());
		close.managedProperty().bind(close.visibleProperty());
		close.visibleProperty().bind(showCloseProperty());
		
		HBox toolbar = new HBox(5);
		toolbar.getStyleClass().add(DECORATION_TOP_STYLE_CLASS);
		toolbar.getChildren().addAll(title, spacer, close);
		pane.setTop(toolbar);
		
		initDraggable(toolbar, close);
		
		// if (isResizableDialog()) {
		Rectangle resizeCorner = new Rectangle(10, 10);
		resizeCorner.getStyleClass().add(DECORATION_RESIZE_STYLE_CLASS);
		resizeCorner.setManaged(false);
		resizeCorner.visibleProperty().bind(resizeableDialogProperty());
		
		initResizable(resizeCorner);
		
		StackPane center = new StackPane() {
			@Override
			protected void layoutChildren() {
				super.layoutChildren();
				if (resizeCorner != null) {
					resizeCorner.relocate(getWidth() - 10, getHeight() - 10);
					resizeCorner.toFront();
				}
			}
		};
		center.getStyleClass().add(DECORATION_CENTER_STYLE_CLASS);
		if (content != null)
			center.getChildren().add(content);
		center.getChildren().add(resizeCorner);
		// center.getChildren().addAll(content, resizeCorner);
		pane.setCenter(center);
		// } else {
		// StackPane center = new StackPane();
		// center.getStyleClass().add(DECORATION_CENTER_STYLE_CLASS);
		// center.getChildren().addAll(content);
		// pane.setCenter(center);
		// }
		return pane;
	}
	
	private double mouseDragDeltaX = 0;
	private double mouseDragDeltaY = 0;
	
	private void initDraggable(final Region toolbar, Button close) {
		toolbar.setOnMouseEntered(event -> {
			if (!isDraggable()) {
				toolbar.setCursor(Cursor.DEFAULT);
				return;
			}
			toolbar.setCursor(Cursor.MOVE);
		});
		toolbar.setOnMouseExited(event -> toolbar.setCursor(Cursor.DEFAULT));
		toolbar.setOnMousePressed(event -> {
			if (!isDraggable())
				return;
			mouseDragDeltaX = contentHolder.getTranslateX() - event.getSceneX();
			mouseDragDeltaY = contentHolder.getTranslateY() - event.getSceneY();
			contentHolder.setCache(true);
		});
		toolbar.setOnMouseDragged(event -> {
			if (!isDraggable())
				return;
			if (contentHolder.translateXProperty().isBound())
				contentHolder.translateXProperty().unbind();
			if (contentHolder.translateYProperty().isBound())
				contentHolder.translateYProperty().unbind();
			Pair<Double, Double> pair = calculateNextValues(event);
			contentHolder.setTranslateX(pair.getKey());
			contentHolder.setTranslateY(pair.getValue());
		});
		toolbar.setOnMouseReleased(event -> {
			if (!isDraggable())
				return;
			contentHolder.setCache(false);
		});
		// fix cursor for close button when the dialog is draggable
		close.setOnMouseEntered(event -> close.setCursor(Cursor.DEFAULT));
		close.setOnMouseExited(event -> close.setCursor(null));
	}
	
	private Pair<Double, Double> calculateNextValues(MouseEvent event) {
		
		final double w = contentHolder.getWidth();
		final double h = contentHolder.getHeight();
		
		final double DROP_SHADOW_SIZE = (contentHolder.getBoundsInParent().getWidth() - contentHolder.getLayoutBounds().getWidth()) / 2.0;
		final Insets padding = contentHolder.getPadding();
		final double rightPadding = padding.getRight();
		final double bottomPadding = padding.getBottom();
		
		// ((dialogContainer.getLayoutBounds().getWidth() / 2.0) - contentHolder.getLayoutBounds().getWidth() / 2.0) * -1.0;
		double minX = 0;
		double maxX = (dialogContainer.getLayoutBounds().getWidth());
		double newX = event.getSceneX() + mouseDragDeltaX;
		newX = org.controlsfx.tools.Utils.clamp(minX, newX, maxX - w + DROP_SHADOW_SIZE + rightPadding + minX);
		
		// ((dialogContainer.getLayoutBounds().getHeight() / 2.0) - contentHolder.getLayoutBounds().getHeight() / 2.0) * -1.0;
		double minY = 0;
		double maxY = (dialogContainer.getLayoutBounds().getHeight());
		double newY = event.getSceneY() + mouseDragDeltaY;
		newY = org.controlsfx.tools.Utils.clamp(minY, newY, maxY - h + DROP_SHADOW_SIZE + bottomPadding + minY);
		
		return new Pair<>(newX, newY);
	}
	
	private void initResizable(final Rectangle resizeCorner) {
		
		// add window resizing
		EventHandler<MouseEvent> resizeHandler = new EventHandler<MouseEvent>() {
			
			private double width;
			private double height;
			private Point2D dragAnchor;
			
			@Override
			public void handle(MouseEvent event) {
				if (!isResizableDialog())
					return;
				EventType<? extends MouseEvent> type = event.getEventType();
				if (type == MouseEvent.MOUSE_PRESSED) {
					width = content.getWidth();
					height = content.getHeight();
					dragAnchor = new Point2D(event.getSceneX(), event.getSceneY());
				} else if (type == MouseEvent.MOUSE_DRAGGED) {
					double calcWidth = Math.max(content.minWidth(-1), width + (event.getSceneX() - dragAnchor.getX()));
					double maxWidth = SimpleDialog.this.getWidth();
					double calcHeight = Math.max(content.minHeight(-1), height + (event.getSceneY() - dragAnchor.getY()));
					double maxHeight = SimpleDialog.this.getHeight();
					content.setPrefWidth(calcWidth > maxWidth ? maxWidth : calcWidth);
					content.setPrefHeight(calcHeight > maxHeight ? maxHeight : calcHeight);
				} else if (type == MouseEvent.MOUSE_ENTERED) {
					resizeCorner.setCursor(Cursor.SE_RESIZE);
				} else if (type == MouseEvent.MOUSE_EXITED) {
					resizeCorner.setCursor(Cursor.DEFAULT);
				}
			}
		};
		
		resizeCorner.setOnMousePressed(resizeHandler);
		resizeCorner.setOnMouseDragged(resizeHandler);
		resizeCorner.setOnMouseEntered(resizeHandler);
		resizeCorner.setOnMouseExited(resizeHandler);
	}
	
	private KeyFrame buildKeyFrame1(AnimationStrategy strategy) {
		switch (strategy == null ? AnimationStrategy.NONE : strategy) {
		case SCALE_FADE:
			return new KeyFrame(Duration.ZERO, new KeyValue(contentHolder.scaleXProperty(), 0, Interpolator.EASE_BOTH),
					new KeyValue(contentHolder.scaleYProperty(), 0, Interpolator.EASE_BOTH),
					new KeyValue(SimpleDialog.this.visibleProperty(), false, Interpolator.EASE_BOTH));
		case SCALE:
			return new KeyFrame(Duration.ZERO, new KeyValue(contentHolder.scaleXProperty(), 0, Interpolator.EASE_BOTH),
					new KeyValue(contentHolder.scaleYProperty(), 0, Interpolator.EASE_BOTH));
		case FADE:
			return new KeyFrame(Duration.ZERO, new KeyValue(SimpleDialog.this.visibleProperty(), false, Interpolator.EASE_BOTH));
		case NONE:
			// intended fall-through
		default:
			return new KeyFrame(Duration.ZERO, new KeyValue(SimpleDialog.this.visibleProperty(), true, Interpolator.EASE_BOTH));
		}
	}
	
	private KeyFrame buildKeyFrame2(AnimationStrategy strategy) {
		switch (strategy == null ? AnimationStrategy.NONE : strategy) {
		case SCALE_FADE:
			// intended fall-through
		case FADE:
			return new KeyFrame(Duration.millis(10), new KeyValue(SimpleDialog.this.visibleProperty(), true, Interpolator.EASE_BOTH),
					new KeyValue(SimpleDialog.this.opacityProperty(), 0, Interpolator.EASE_BOTH));
		case SCALE:
			// intended fall-through
		case NONE:
			// intended fall-through
		default:
			return new KeyFrame(Duration.ONE, new KeyValue(SimpleDialog.this.visibleProperty(), true, Interpolator.EASE_BOTH),
					new KeyValue(SimpleDialog.this.opacityProperty(), 1, Interpolator.EASE_BOTH));
		}
	}
	
	private KeyFrame buildKeyFrame3(AnimationStrategy strategy, Duration duration) {
		switch (strategy == null ? AnimationStrategy.NONE : strategy) {
		case SCALE_FADE:
			return new KeyFrame(duration, new KeyValue(contentHolder.scaleXProperty(), 1, Interpolator.EASE_BOTH),
					new KeyValue(contentHolder.scaleYProperty(), 1, Interpolator.EASE_BOTH),
					new KeyValue(SimpleDialog.this.opacityProperty(), 1, Interpolator.EASE_BOTH));
		case SCALE:
			return new KeyFrame(duration, new KeyValue(contentHolder.scaleXProperty(), 1, Interpolator.EASE_BOTH),
					new KeyValue(contentHolder.scaleYProperty(), 1, Interpolator.EASE_BOTH),
					new KeyValue(SimpleDialog.this.opacityProperty(), 1, Interpolator.EASE_BOTH));
		case FADE:
			return new KeyFrame(duration, new KeyValue(SimpleDialog.this.opacityProperty(), 1, Interpolator.EASE_BOTH));
		case NONE:
			// intended fall-through
		default:
			return new KeyFrame(Duration.ZERO, new KeyValue(contentHolder.scaleXProperty(), 1, Interpolator.EASE_BOTH),
					new KeyValue(contentHolder.scaleYProperty(), 1, Interpolator.EASE_BOTH),
					new KeyValue(SimpleDialog.this.opacityProperty(), 1, Interpolator.EASE_BOTH));
		}
	}
	
	private class CenterTransition extends CachedTransition {
		public CenterTransition(Duration duration) {
			super(contentHolder, new Timeline(buildKeyFrame1(animationStrategy.get()), buildKeyFrame2(animationStrategy.get()),
					buildKeyFrame3(animationStrategy.get(), duration)));
			// reduce the number to increase the shifting , increase number to reduce shifting
			setCycleDuration(Duration.seconds(0.4));
			setDelay(Duration.seconds(0));
		}
	}
	
	private class UncachedCenterTransition extends Transition {
		
		final Timeline timeline;
		
		public UncachedCenterTransition(Duration duration) {
			timeline = new Timeline(buildKeyFrame1(animationStrategy.get()), buildKeyFrame2(animationStrategy.get()),
					buildKeyFrame3(animationStrategy.get(), duration));
			setCycleDuration(Duration.seconds(0.4));
			setDelay(Duration.seconds(0));
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void interpolate(double d) {
			timeline.playFrom(Duration.seconds(d));
			timeline.stop();
		}
	}
}
