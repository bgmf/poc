package eu.dzim.shared.fx.ui;

import eu.dzim.shared.fx.util.ProgressTranistion;
import eu.dzim.shared.fx.util.UIComponentType;
import eu.dzim.shared.util.SingleAcceptor;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

public class SwipePane extends StackPane {

    private static final Logger LOG = LogManager.getLogger(SwipePane.class);
    private final Set<Node> nodeList = new HashSet<>();
    private final PseudoClass selected = PseudoClass.getPseudoClass("selected");
    private final ObservableList<SwipePanePlaceholder<?>> panes = FXCollections.observableArrayList();
    private final ObservableList<SwipePaneCircleButton> circles = FXCollections.observableArrayList();
    private final IntegerProperty index = new SimpleIntegerProperty(this, "index", -1);
    private final ObjectProperty<Duration> duration = new SimpleObjectProperty<>(this, "duration", Duration.millis(250.0));
    private final BooleanProperty useControlOpacity = new SimpleBooleanProperty(this, "useControlOpacity", true);
    private final BooleanProperty usePanelOpacity = new SimpleBooleanProperty(this, "usePanelOpacity", false);
    private final BooleanProperty usePanelClipping = new SimpleBooleanProperty(this, "usePanelClipping", true);
    private final ObjectProperty<Orientation> orientation = new SimpleObjectProperty<>(this, "orientation", Orientation.HORIZONTAL);
    private final DoubleProperty scrollRestriction = new SimpleDoubleProperty(this, "scrollRestriction", 0.0);
    @FXML private ProgressBar progress;
    @FXML private HBox circleBox;
    @FXML private Button back;
    @FXML private Button close;
    private LoadRestrictionType loadRestrictionType = LoadRestrictionType.ONE;
    private SingleAcceptor<Void> onBackAcceptor = getDefaultOnBackAcceptor();
    private SingleAcceptor<Void> onCloseAcceptor = getDefaultOnCloseAcceptor();
    private boolean panesInitialized = false;
    private boolean drag = false;
    // horizontal orientation
    private double dragX = 0.0;
    private Map<Node, Double> paneToTranslateXMapping = new HashMap<>();
    // vertical orientation
    private double dragY = 0.0;
    private Map<Node, Double> paneToTranslateYMapping = new HashMap<>();
    public SwipePane() {
        super();
        try {
            getLoader(SharedUIComponentType.SWIPE_PANE).load();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static final SingleAcceptor<Void> getDefaultOnCloseAcceptor() {
        return v -> {
        };
    }

    private static final SingleAcceptor<Void> getDefaultOnBackAcceptor() {
        return v -> {
        };
    }

    private static void clipChildren(Region region) {
        final Rectangle outputClip = new Rectangle();
        region.setClip(outputClip);
        region.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
            outputClip.setWidth(newValue.getWidth());
            outputClip.setHeight(newValue.getHeight());
        });
    }

    private FXMLLoader getLoader(UIComponentType component) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(component.getAbsoluteLocation()));
        loader.setRoot(this);
        loader.setController(this);
        return loader;
    }

    @FXML
    private void initialize() {

        panes.addListener(this::handlePaneListChanges);
        circles.addListener(this::handleCircleListChanges);
        index.addListener(this::handleIndexChanges);

        progress.setProgress(0.0);

        progress.managedProperty().bind(progress.visibleProperty());
        circleBox.managedProperty().bind(circleBox.visibleProperty());
        back.managedProperty().bind(back.visibleProperty());
        close.managedProperty().bind(close.visibleProperty());

        orientation.addListener((obs, o, n) -> {
            if (n == null)
                orientation.set(o); // prevent null values
        });

        layoutBoundsProperty().addListener((obs, o, n) -> {
            initLayout();
            initPanelOpacity();
        });

        if (isUsePanelClipping())
            clipChildren(this);

        setOnTouchPressed(this::handleTouchPressed);
        setOnTouchMoved(this::handleTouchMoved);
        setOnTouchReleased(this::handleTouchReleased);

        setOnMousePressed(this::handleMousePressed);
        setOnMouseDragged(this::handleMouseDragged);
        setOnMouseReleased(this::handleMouseReleased);

        setOnKeyReleased(this::handleKeyReleased);

        nodeList.addAll(Arrays.asList(progress, circleBox, back, close));

        back.setOnAction(e -> {
            onBackAcceptor.accept(null);
        });
        close.setOnAction(e -> {
            onCloseAcceptor.accept(null);
        });
    }

    public ProgressBar getProgress() {
        return progress;
    }

    public HBox getCircleBox() {
        return circleBox;
    }

    public Button getBack() {
        return back;
    }

    public Button getClose() {
        return close;
    }

    public final ObservableList<SwipePanePlaceholder<?>> getPanes() {
        return panes;
    }

    public final IntegerProperty indexProperty() {
        return index;
    }

    public final int getIndex() {
        return indexProperty().get();
    }

    public final void setIndex(int index) {
        indexProperty().set(index);
    }

    public final ObjectProperty<Duration> durationProperty() {
        return duration;
    }

    public final Duration getDuration() {
        return durationProperty().get();
    }

    public final void setDuration(Duration duration) {
        durationProperty().set(duration);
    }

    public final void setOnBackAcceptor(SingleAcceptor<Void> onBackAcceptor) {
        this.onBackAcceptor = onBackAcceptor == null ? getDefaultOnBackAcceptor() : onBackAcceptor;
    }

    public final void setOnCloseAcceptor(SingleAcceptor<Void> onCloseAcceptor) {
        this.onCloseAcceptor = onCloseAcceptor == null ? getDefaultOnCloseAcceptor() : onCloseAcceptor;
    }

    public final BooleanProperty progressVisibleProperty() {
        return progress.visibleProperty();
    }

    public final boolean getProgressVisible() {
        return progressVisibleProperty().get();
    }

    public final void setProgressVisible(boolean visible) {
        progressVisibleProperty().set(visible);
    }

    public final BooleanProperty circlesVisibleProperty() {
        return circleBox.visibleProperty();
    }

    public final boolean getCirclesVisible() {
        return circlesVisibleProperty().get();
    }

    public final void setCirclesVisible(boolean visible) {
        circlesVisibleProperty().set(visible);
    }

    public final BooleanProperty backVisibleProperty() {
        return back.visibleProperty();
    }

    public final boolean getBackVisible() {
        return backVisibleProperty().get();
    }

    public final void setBackVisible(boolean visible) {
        backVisibleProperty().set(visible);
    }

    public final BooleanProperty closeVisibleProperty() {
        return close.visibleProperty();
    }

    public final boolean getCloseVisible() {
        return closeVisibleProperty().get();
    }

    public final void setCloseVisible(boolean visible) {
        closeVisibleProperty().set(visible);
    }

    public final BooleanProperty useControlOpacityProperty() {
        return this.useControlOpacity;
    }

    public final boolean isUseControlOpacity() {
        return this.useControlOpacityProperty().get();
    }

    public final void setUseControlOpacity(final boolean useControlOpacity) {
        this.useControlOpacityProperty().set(useControlOpacity);
    }

    public final BooleanProperty usePanelOpacityProperty() {
        return this.usePanelOpacity;
    }

    public final boolean isUsePanelOpacity() {
        return this.usePanelOpacityProperty().get();
    }

    public final void setUsePanelOpacity(final boolean usePanelOpacity) {
        this.usePanelOpacityProperty().set(usePanelOpacity);
    }

    public final BooleanProperty usePanelClippingProperty() {
        return this.usePanelClipping;
    }

    public final boolean isUsePanelClipping() {
        return this.usePanelClippingProperty().get();
    }

    public final void setUsePanelClipping(final boolean usePanelClipping) {
        this.usePanelClippingProperty().set(usePanelClipping);
    }

    public final ObjectProperty<Orientation> orientationProperty() {
        return this.orientation;
    }

    public final Orientation getOrientation() {
        return this.orientationProperty().get();
    }

    public final void setOrientation(final Orientation orientation) {
        this.orientationProperty().set(orientation);
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

    public LoadRestrictionType getLoadRestrictionType() {
        return loadRestrictionType;
    }

    public void setLoadRestrictionType(LoadRestrictionType loadRestrictionType) {
        if (loadRestrictionType == null)
            this.loadRestrictionType = LoadRestrictionType.ONE;
        else
            this.loadRestrictionType = loadRestrictionType;
    }

    private void handlePaneListChanges(ListChangeListener.Change<? extends SwipePanePlaceholder<?>> change) {

        while (change.next()) {

            List<? extends SwipePanePlaceholder<?>> removed = change.getRemoved();
            for (SwipePanePlaceholder<?> pane : removed) {
                pane.setShow(false);
                int index = getChildren().indexOf(pane);
                SwipePaneCircleButton button = (SwipePaneCircleButton) circleBox.lookup("#" + index);
                if (button == null)
                    continue;
                circles.remove(button);
            }
            getChildren().removeAll(removed);

            List<? extends SwipePanePlaceholder<?>> added = change.getAddedSubList();
            int index = getChildren().indexOf(progress);
            getChildren().addAll(index, added);
            for (SwipePanePlaceholder<?> pane : added) {
                SwipePaneCircleButton b = new SwipePaneCircleButton();
                b.setId("" + getChildren().indexOf(pane));
                b.setOnAction(e -> {
                    setIndex(Integer.parseInt(b.getId()));
                });
                circles.add(b);
                pane.setCache(true);
                pane.setCacheHint(CacheHint.SPEED);
            }

            if (getIndex() == -1)
                setIndex(0);

            progress.setProgress(getIndex() / (panes.size() - 1.0));
        }

        initLayout();
        initPanelOpacity();
    }

    private void handleCircleListChanges(ListChangeListener.Change<? extends SwipePaneCircleButton> change) {

        while (change.next()) {

            List<? extends SwipePaneCircleButton> removed = change.getRemoved();
            circleBox.getChildren().removeAll(removed);

            List<? extends SwipePaneCircleButton> added = change.getAddedSubList();
            circleBox.getChildren().addAll(added);
        }
    }

    private void handleIndexChanges(ObservableValue<? extends Number> obs, Number o, Number n) {
        // if (!drag)
        // initLayout();

        final boolean _drag = drag;

        ParallelTransition parallel = new ParallelTransition();
        parallel.setAutoReverse(false);
        parallel.setCycleCount(1);

        if (isUseControlOpacity()) {
            if (!_drag && back.isVisible())
                buildOpacityTransition(parallel, back, back.getOpacity(), 0.0, 3.0, false);
            if (!_drag && close.isVisible())
                buildOpacityTransition(parallel, close, close.getOpacity(), 0.0, 3.0, false);
        }

        for (int i = 0; i < circles.size(); i++) {
            circles.get(i).pseudoClassStateChanged(selected, i == getIndex());
            if (Orientation.HORIZONTAL == getOrientation())
                buildStandardXTransitions(parallel);
            else if (Orientation.VERTICAL == getOrientation())
                buildStandardYTransitions(parallel);
        }

        if (isUseControlOpacity()) {
            if (back.isVisible())
                buildOpacityTransition(parallel, back, back.getOpacity(), 1.0, _drag ? 1.0 : 3.0, _drag ? false : true);
            if (close.isVisible())
                buildOpacityTransition(parallel, close, close.getOpacity(), 1.0, _drag ? 1.0 : 3.0, _drag ? false : true);
        }

        buildProgressTransition(parallel);

        parallel.playFromStart();

        parallel.setOnFinished(e -> {
            int index = n.intValue();
            if (panes.size() > 0) {
                for (int i = 0; i < panes.size(); i++) {
                    // panes.get(i).setShow(i == index - 1 || i == index || i == index + 1);
                    // panes.get(i).setShow(i == index);
                    panes.get(i).setShow(showPageOnIndex(index, i));
                    panes.get(i).setCache(i == index ? false : true);
                    panes.get(i).setCacheHint(i == index ? CacheHint.DEFAULT : CacheHint.SPEED);
                }
                panes.get(index).requestFocus();
            }
        });
    }

    private boolean showPageOnIndex(int current, int index) {
        if (index == current)
            return true;
        LoadRestrictionType type = loadRestrictionType == null ? LoadRestrictionType.ONE : loadRestrictionType;
        int value = type.value;
        for (int i = current - value; i <= current + value; i++) {
            if (index == i)
                return true;
        }
        return false;
    }

    private void initLayout() {

        int index = getIndex() * -1;
        for (Node child : getChildren()) {
            if (nodeList.contains(child))
                continue;
            if (Orientation.HORIZONTAL == getOrientation())
                child.setTranslateX(index * getWidth());
            else if (Orientation.VERTICAL == getOrientation())
                child.setTranslateY(index * getHeight());
            index++;
        }

        if (!panesInitialized && panes.size() > 0) {
            circles.get(getIndex()).pseudoClassStateChanged(selected, true);
            progress.setProgress(getIndex() / (panes.size() - 1.0));
            panesInitialized = true;
        }
    }

    private void initPanelOpacity() {
        for (Pane p : panes) {
            p.opacityProperty().unbind();
            if (isUsePanelOpacity()) {
                if (Orientation.HORIZONTAL == getOrientation()) {
                    DoubleBinding binding = Bindings.createDoubleBinding(() -> {
                        double width = getWidth();
                        double translateX = p.getTranslateX();
                        double opacityBase = Math.abs(translateX) / (width + width / 3);

                        // if (translateX < (-1.0 * width + width / 3) || translateX > (2 * (width / 3))) {
                        // return 0.0;
                        // }

                        // if (translateX < (-1.0 * width + width / 3)) {
                        // return 1.0 - opacityBase;
                        // } else if (translateX > (2 * (width / 3))) {
                        // return 1.0 - opacityBase;
                        // }
                        // return 1.0;

                        return 1.0 - opacityBase;
                    }, p.translateXProperty());
                    p.opacityProperty().bind(binding);
                } else if (Orientation.VERTICAL == getOrientation()) {
                    // TODO
                }
            }
        }
    }

    private void handleTouchPressed(TouchEvent event) {
        if (Orientation.HORIZONTAL == getOrientation())
            startX(event.getTouchPoint().getX());
        else if (Orientation.VERTICAL == getOrientation())
            startY(event.getTouchPoint().getY());
        event.consume();
    }

    private void handleTouchMoved(TouchEvent event) {
        if (Orientation.HORIZONTAL == getOrientation())
            dragX(event.getTouchPoint().getX());
        else if (Orientation.VERTICAL == getOrientation())
            dragY(event.getTouchPoint().getY());
        event.consume();
    }

    private void handleTouchReleased(TouchEvent event) {
        if (Orientation.HORIZONTAL == getOrientation())
            endX(event.getTouchPoint().getX());
        else if (Orientation.VERTICAL == getOrientation())
            endY(event.getTouchPoint().getY());
        event.consume();
    }

    private void handleMousePressed(MouseEvent event) {
        if (Orientation.HORIZONTAL == getOrientation())
            startX(event.getX());
        else if (Orientation.VERTICAL == getOrientation())
            startY(event.getY());
        event.consume();
    }

    private void handleMouseDragged(MouseEvent event) {
        if (Orientation.HORIZONTAL == getOrientation())
            dragX(event.getX());
        else if (Orientation.VERTICAL == getOrientation())
            dragY(event.getY());
        event.consume();
    }

    private void handleMouseReleased(MouseEvent event) {
        if (Orientation.HORIZONTAL == getOrientation())
            endX(event.getX());
        else if (Orientation.VERTICAL == getOrientation())
            endY(event.getY());
        event.consume();
    }

    private void handleKeyReleased(KeyEvent event) {
        final int index = getIndex();
        final int size = panes.size();
        if (Orientation.HORIZONTAL == orientation.get()) {
            if (KeyCode.LEFT == event.getCode() || KeyCode.KP_LEFT == event.getCode()) {
                if ((index - 1) >= 0) {
                    setIndex(index - 1);
                    event.consume();
                }
            } else if (KeyCode.RIGHT == event.getCode() || KeyCode.KP_RIGHT == event.getCode()) {
                if ((index + 1) < size) {
                    setIndex(index + 1);
                    event.consume();
                }
            }
        } else if (Orientation.VERTICAL == orientation.get()) {
            if (KeyCode.UP == event.getCode() || KeyCode.KP_UP == event.getCode()) {
                if ((index - 1) >= 0) {
                    setIndex(index - 1);
                    event.consume();
                }
            } else if (KeyCode.DOWN == event.getCode() || KeyCode.KP_DOWN == event.getCode()) {
                if ((index + 1) < size) {
                    setIndex(index + 1);
                    event.consume();
                }
            }
        }
    }

    private double getRelevantScrollRestriction() {

        double _global = getScrollRestriction();
        int _index = getIndex();

        double global = _global > 1.0 ? 1.0 : _global;
        double pane;
        if (panes == null || panes.isEmpty() || _index < 0 || _index >= panes.size() || panes.get(_index) == null)
            pane = global;
        else
            pane = panes.get(_index).getScrollRestriction() > 1.0 ? 1.0 : panes.get(_index).getScrollRestriction();

        return Math.min(global, pane);
    }

    private void startX(double x) {

        if (drag)
            return;

        double width = getWidth();
        double tolerance = width * getRelevantScrollRestriction();
        if (x > tolerance && x < (width - tolerance))
            return;

        // LOG.info(String.format(Locale.ROOT, "[START] x=%.2f / current-index=%d", x, getIndex()));

        drag = true;
        dragX = x;
        paneToTranslateXMapping.clear();
        iterateChildren(child -> paneToTranslateXMapping.put(child, child.getTranslateX()));
    }

    private void startY(double y) {

        if (drag)
            return;

        double height = getHeight();
        double tolerance = height * getRelevantScrollRestriction();
        if (y > tolerance && y < (height - tolerance))
            return;

        // LOG.info(String.format(Locale.ROOT, "[START] y=%.2f / current-index=%d", y, getIndex()));

        drag = true;
        dragY = y;
        paneToTranslateYMapping.clear();
        iterateChildren(child -> paneToTranslateYMapping.put(child, child.getTranslateY()));
    }

    private void dragX(double x) {

        if (!drag)
            return;

        double deltaX = dragX - x;
        double mult = deltaX < 0 ? -1.0 : 1.0;
        double width = getWidth();

        // LOG.info(String.format(Locale.ROOT, "[INTERMEDIATE] deltaX=%.2f / mult=%.1f / current-index=%d", deltaX, mult, getIndex()));

        double opacityBase = Math.abs(deltaX) / (width / 3);
        double controlOpacity = 1.0 - opacityBase;

        if (deltaX < 0) {
            // drag right
            // we are already at the most left node
            if (getIndex() == 0 && (Math.abs(deltaX) > getWidth() / 5)) {
                deltaX = mult * getWidth() / 5;
            } else if (Math.abs(deltaX) > getWidth()) {
                deltaX = mult * getWidth();
            }
        } else {
            // drag left
            // we are already at the most right node
            if (getIndex() == panes.size() - 1 && (Math.abs(deltaX) > getWidth() / 5)) {
                deltaX = mult * getWidth() / 5;
            } else if (Math.abs(deltaX) > getWidth()) {
                deltaX = mult * getWidth();
            }
        }

        if (isUseControlOpacity()) {
            if (back.isVisible())
                back.setOpacity(controlOpacity);
            if (close.isVisible())
                close.setOpacity(controlOpacity);
        }

        final double dX = deltaX;
        iterateChildren(child -> {
            Double d = paneToTranslateXMapping.get(child);
            if (d != null)
                child.setTranslateX(d - dX);
        });
    }

    private void dragY(double y) {

        if (!drag)
            return;

        double deltaY = dragY - y;
        double mult = deltaY < 0 ? -1.0 : 1.0;
        double height = getHeight();

        // LOG.info(String.format(Locale.ROOT, "[INTERMEDIATE] deltaY=%.2f / mult=%.1f / current-index=%d", deltaY, mult, getIndex()));

        double opacityBase = Math.abs(deltaY) / (height / 3);
        double controlOpacity = 1.0 - opacityBase;

        if (deltaY < 0) {
            // drag right
            // we are already at the topmost node
            if (getIndex() == 0 && (Math.abs(deltaY) > getHeight() / 5)) {
                deltaY = mult * getHeight() / 5;
            } else if (Math.abs(deltaY) > getHeight()) {
                deltaY = mult * getHeight();
            }
        } else {
            // drag left
            // we are already at the most bottom node
            if (getIndex() == panes.size() - 1 && (Math.abs(deltaY) > getHeight() / 5)) {
                deltaY = mult * getHeight() / 5;
            } else if (Math.abs(deltaY) > getHeight()) {
                deltaY = mult * getHeight();
            }
        }

        if (isUseControlOpacity()) {
            if (back.isVisible())
                back.setOpacity(controlOpacity);
            if (close.isVisible())
                close.setOpacity(controlOpacity);
        }

        final double dY = deltaY;
        iterateChildren(child -> {
            Double d = paneToTranslateYMapping.get(child);
            if (d != null)
                child.setTranslateY(d - dY);
        });
    }

    private void endX(double x) {

        if (!drag)
            return;

        ParallelTransition parallel = new ParallelTransition();
        parallel.setAutoReverse(false);
        parallel.setCycleCount(1);

        double deltaX = dragX - x;
        double width = getWidth();
        boolean snap = Math.abs(deltaX) >= (width / 3);

        // LOG.info(String.format(Locale.ROOT, "[END] deltaX=%.2f / parent-width=%.2f / current-index=%d / snap?=%b", deltaX, width, getIndex(),
        // snap));

        if (deltaX < 0) {
            // drag right
            // we are already at the most left node
            if (getIndex() == 0) {
                buildStandardXTransitions(parallel);
            }
            // snap to next node on the left
            else if (snap) {
                setIndex(getIndex() - 1);
            }
            // return to original position
            else {
                buildReturnToStartXTransitions(parallel);
            }

        } else {
            // drag left
            // we are already at the most right node
            if (getIndex() == panes.size() - 1) {
                buildStandardXTransitions(parallel);
            }
            // snap to next node on the right
            else if (snap) {
                setIndex(getIndex() + 1);
            }
            // return to original position
            else {
                buildReturnToStartXTransitions(parallel);
            }
        }

        if (isUseControlOpacity()) {
            if (back.isVisible())
                buildOpacityTransition(parallel, back, back.getOpacity(), 1.0, 1.0, false);
            if (close.isVisible())
                buildOpacityTransition(parallel, close, close.getOpacity(), 1.0, 1.0, false);
        }

        buildProgressTransition(parallel);

        if (!parallel.getChildren().isEmpty())
            parallel.playFromStart();

        drag = false;
        dragX = 0.0;
        paneToTranslateXMapping.clear();
    }

    private void endY(double y) {

        if (!drag)
            return;

        ParallelTransition parallel = new ParallelTransition();
        parallel.setAutoReverse(false);
        parallel.setCycleCount(1);

        double deltaY = dragY - y;
        double height = getHeight();
        boolean snap = Math.abs(deltaY) >= (height / 3);

        // LOG.info(String.format(Locale.ROOT, "[END] deltaY=%.2f / parent-height=%.2f / current-index=%d / snap?=%b", deltaY, height, getIndex(),
        // snap));

        if (deltaY < 0) {
            // drag right
            // we are already at the most left node
            if (getIndex() == 0) {
                buildStandardYTransitions(parallel);
            }
            // snap to next node on the left
            else if (snap) {
                setIndex(getIndex() - 1);
            }
            // return to original position
            else {
                buildReturnToStartYTransitions(parallel);
            }

        } else {
            // drag left
            // we are already at the most right node
            if (getIndex() == panes.size() - 1) {
                buildStandardYTransitions(parallel);
            }
            // snap to next node on the right
            else if (snap) {
                setIndex(getIndex() + 1);
            }
            // return to original position
            else {
                buildReturnToStartYTransitions(parallel);
            }
        }

        if (isUseControlOpacity()) {
            if (back.isVisible())
                buildOpacityTransition(parallel, back, back.getOpacity(), 1.0, 1.0, false);
            if (close.isVisible())
                buildOpacityTransition(parallel, close, close.getOpacity(), 1.0, 1.0, false);
        }

        buildProgressTransition(parallel);

        if (!parallel.getChildren().isEmpty())
            parallel.playFromStart();

        drag = false;
        dragY = 0.0;
        paneToTranslateYMapping.clear();
    }

    private void iterateChildren(SingleAcceptor<Node> acceptor) {
        for (Node child : getChildren()) {
            if (nodeList.contains(child))
                continue;
            acceptor.accept(child);
        }
    }

    private void buildStandardXTransitions(final ParallelTransition parallel) {
        int index = getIndex() * -1;
        for (Node child : getChildren()) {
            if (nodeList.contains(child))
                continue;
            parallel.getChildren().add(buildTranslateXTransition(child, child.getTranslateX(), index * getWidth()));
            index++;
        }
    }

    private void buildReturnToStartXTransitions(final ParallelTransition parallel) {
        iterateChildren(child -> {
            Double d = paneToTranslateXMapping.get(child);
            if (d != null) {
                parallel.getChildren().add(buildTranslateXTransition(child, child.getTranslateX(), d));
            }
        });
    }

    private TranslateTransition buildTranslateXTransition(Node child, double fromValue, double toValue) {
        TranslateTransition tr = new TranslateTransition(getDuration(), child);
        tr.setAutoReverse(false);
        tr.setCycleCount(1);
        tr.setFromX(fromValue);
        tr.setToX(toValue);
        return tr;
    }

    private void buildStandardYTransitions(final ParallelTransition parallel) {
        int index = getIndex() * -1;
        for (Node child : getChildren()) {
            if (nodeList.contains(child))
                continue;
            parallel.getChildren().add(buildTranslateYTransition(child, child.getTranslateY(), index * getHeight()));
            index++;
        }
    }

    private void buildReturnToStartYTransitions(final ParallelTransition parallel) {
        iterateChildren(child -> {
            Double d = paneToTranslateYMapping.get(child);
            if (d != null) {
                parallel.getChildren().add(buildTranslateYTransition(child, child.getTranslateY(), d));
            }
        });
    }

    private TranslateTransition buildTranslateYTransition(Node child, double fromValue, double toValue) {
        TranslateTransition tr = new TranslateTransition(getDuration(), child);
        tr.setAutoReverse(false);
        tr.setCycleCount(1);
        tr.setFromY(fromValue);
        tr.setToY(toValue);
        return tr;
    }

    private void buildOpacityTransition(ParallelTransition parallel, Node target, double fromValue, double toValue, double durationDivisor,
            boolean useDelay) {
        Duration duration = getDuration().divide(durationDivisor);
        Duration delay = useDelay ? getDuration().subtract(getDuration().divide(durationDivisor)) : Duration.ZERO;
        FadeTransition tr = new FadeTransition(duration, target);
        tr.setDelay(delay);
        tr.setAutoReverse(false);
        tr.setCycleCount(1);
        tr.setFromValue(fromValue);
        tr.setToValue(toValue);
        parallel.getChildren().add(tr);
    }

    private void buildProgressTransition(final ParallelTransition parallel) {
        if (panes.size() > 0 && (panes.size() - 1.0) > 0) {
            ProgressTranistion progress = new ProgressTranistion(this.progress);
            progress.setDuration(getDuration());
            progress.setAutoReverse(false);
            progress.setCycleCount(1);
            progress.setFrom(this.progress.getProgress());
            progress.setTo(getIndex() / (panes.size() - 1.0));
            parallel.getChildren().add(progress);
        }
    }

    public enum LoadRestrictionType {

        // @formatter:off
		ONE(0), // default, most aggressive - loads only the current page
		THREE(1), // load current page and max one to the left and right of it
		FIVE(2); // load current page and max two to the left and right of it
		// @formatter:on

        private final int value;

        private LoadRestrictionType(final int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
