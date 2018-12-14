package eu.dzim.shared.fx.ui;

import com.sun.javafx.tk.Toolkit;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Effect;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;

/**
 * Copied and adapted combination of <code>FXDialogs</code> and <code>LightweightDialog</code> from the <code>ControlsFX</code> project.
 *
 * @author daniel.zimmermann@cnlab.ch
 * @author original author not named, but part of ControlsFX 8.0.5
 * @see http://grepcode.com/file/repo1.maven.org/maven2/org.controlsfx/controlsfx/8.0.5/org/controlsfx/dialog/LightweightDialog.java/
 * @see http://grepcode.com/file/repo1.maven.org/maven2/org.controlsfx/controlsfx/8.0.5/org/controlsfx/dialog/FXDialog.java#FXDialog
 * @see http://grepcode.com/file/repo1.maven.org/maven2/org.controlsfx/controlsfx/8.0.5/org/controlsfx/dialog/dialogs.css?av=f
 */
public class LightweightDialog {

    /*
     **************************************************************************
     *
     * Static fields
     *
     **************************************************************************/

    protected static final URL DIALOGS_CSS_URL = LightweightDialog.class.getResource("/css/dialogs.css");
    protected static final int HEADER_HEIGHT = 28;

    /*
     **************************************************************************
     *
     * Custom Static fields
     *
     * @author daniel.zimmermann@cnlab.ch
     *
     **************************************************************************/
    /*
     ***************************************************************************
     *
     * Stylesheet Handling
     *
     **************************************************************************/
    protected static final PseudoClass ACTIVE_PSEUDO_CLASS = PseudoClass.getPseudoClass("active");
    private static final ObservableMap<Stage, ObservableList<LightweightDialog>> OPEN_DIALOGS = FXCollections.observableHashMap();

    /*
     **************************************************************************
     *
     * Private fields
     *
     **************************************************************************/
    private final Stage myStage;
    private final Object myLock = new Object();
    private final ObjectProperty<ButtonType> result = new SimpleObjectProperty<>();
    protected BorderPane root;
    protected HBox windowBtns;
    protected Button closeButton;
    protected Button minButton;
    protected Button maxButton;
    protected Rectangle resizeCorner;
    protected Label titleLabel;
    protected ToolBar toolBar;
    protected double mouseDragDeltaX = 0;
    protected double mouseDragDeltaY = 0;
    protected StackPane lightweightDialog;
    // the modal dialog has to be parented to something, which is either a
    // Parent or a Scene. Which one we use is dependent on what the owner is that
    // is passed into the constructor
    private Scene scene;
    private Parent owner;
    private Region opaqueLayer;
    private Region horizontalBackground;
    private Pane dialogStack;
    private Parent originalParent;
    private BooleanProperty focused;
    private StringProperty title;
    private BooleanProperty resizable;

    /*
     **************************************************************************
     *
     * Custom extensions
     *
     * @author daniel.zimmermann@cnlab.ch
     *
     **************************************************************************/
    private Effect effect;
    private Effect tempEffect;
    private boolean modal = true;
    private BooleanProperty draggable;
    private BooleanProperty chromeVisible;
    private BooleanProperty useHorizontalBackground;
    private BooleanProperty useTransition;
    private ObjectProperty<Duration> transitionDuration;

    /*
     **************************************************************************
     *
     * Constructors
     *
     **************************************************************************/

    /**
     * Lightweight dialog created
     *
     * @param title         the title of this dialog
     * @param incomingOwner the owner of this dialog
     * @param buttonTypes   the {@link Button}s of the {@link ButtonBar}, <code>null</code> for none.
     */
    public LightweightDialog(final String title, final Object incomingOwner, ButtonType... buttonTypes) {

        Object _owner = incomingOwner;

        // we need to determine the type of the owner, so that we can appropriately
        // show the dialog
        if (_owner == null) {
            _owner = org.controlsfx.tools.Utils.getWindow(_owner);
        }

        if (_owner instanceof Scene) {
            this.scene = (Scene) _owner;
        } else if (_owner instanceof Stage) {
            this.scene = ((Stage) _owner).getScene();
        } else if (_owner instanceof Tab) {
            // special case for people wanting to show a lightweight dialog inside
            // one tab whilst the rest of the TabPane remains responsive.
            // we keep going up until the styleclass is "tab-content-area"
            owner = (Parent) ((Tab) _owner).getContent();
        } else if (_owner instanceof Node) {
            owner = getFirstParent((Node) _owner);
        } else {
            throw new IllegalArgumentException("Unknown owner: " + _owner.getClass());
        }

        if (scene == null && owner != null) {
            this.scene = owner.getScene();
        }

        myStage = (Stage) scene.getWindow();

        // *** The rest is for adding window decorations ***
        init(title, true, buttonTypes);
        lightweightDialog.getStyleClass().addAll("lightweight", "custom-chrome");

        // add window dragging
        toolBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if (!draggableProperty().get())
                    return;

                mouseDragDeltaX = lightweightDialog.getLayoutX() - event.getSceneX();
                mouseDragDeltaY = lightweightDialog.getLayoutY() - event.getSceneY();
                lightweightDialog.setCache(true);
            }
        });
        toolBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if (!draggableProperty().get())
                    return;

                final double w = lightweightDialog.getWidth();
                final double h = lightweightDialog.getHeight();

                // remove the drop shadow out of the width calculations
                final double DROP_SHADOW_SIZE =
                        (lightweightDialog.getBoundsInParent().getWidth() - lightweightDialog.getLayoutBounds().getWidth()) / 2.0;
                final Insets padding = lightweightDialog.getPadding();
                final double rightPadding = padding.getRight();
                final double bottomPadding = padding.getBottom();

                double minX = 0;
                double maxX = owner == null ? scene.getWidth() : owner.getLayoutBounds().getWidth();
                double newX = event.getSceneX() + mouseDragDeltaX;
                newX = org.controlsfx.tools.Utils.clamp(minX, newX, maxX - w + DROP_SHADOW_SIZE + rightPadding + minX);

                double minY = 0;
                double maxY = owner == null ? scene.getHeight() : owner.getLayoutBounds().getHeight();
                double newY = event.getSceneY() + mouseDragDeltaY;
                newY = org.controlsfx.tools.Utils.clamp(0, newY, maxY - h + DROP_SHADOW_SIZE + bottomPadding + minY);

                lightweightDialog.setLayoutX(newX);
                lightweightDialog.setLayoutY(newY);
            }
        });
        toolBar.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if (!draggableProperty().get())
                    return;

                lightweightDialog.setCache(false);
            }
        });

        // we don't support maximising or minimising lightweight dialogs, so we
        // remove these from the toolbar
        minButton = null;
        maxButton = null;
        windowBtns.getChildren().setAll(closeButton);

        // add window resizing
        EventHandler<MouseEvent> resizeHandler = new EventHandler<MouseEvent>() {
            private double width;
            private double height;
            private Point2D dragAnchor;

            @Override
            public void handle(MouseEvent event) {
                EventType<? extends MouseEvent> type = event.getEventType();

                if (type == MouseEvent.MOUSE_PRESSED) {
                    width = lightweightDialog.getWidth();
                    height = lightweightDialog.getHeight();
                    dragAnchor = new Point2D(event.getSceneX(), event.getSceneY());
                } else if (type == MouseEvent.MOUSE_DRAGGED) {
                    double calcWidth = Math.max(lightweightDialog.minWidth(-1), width + (event.getSceneX() - dragAnchor.getX()));
                    double maxWidth = ((Pane) root.getCenter()).getMaxWidth();
                    double calcHeight = Math.max(lightweightDialog.minHeight(-1), height + (event.getSceneY() - dragAnchor.getY()));
                    double maxHeight = ((Pane) root.getCenter()).getMaxHeight();
                    lightweightDialog.setPrefWidth(calcWidth > maxWidth ? maxWidth : calcWidth);
                    lightweightDialog.setPrefHeight(calcHeight > maxHeight ? maxHeight : calcHeight);
                }
            }
        };
        resizeCorner.setOnMousePressed(resizeHandler);
        resizeCorner.setOnMouseDragged(resizeHandler);

        // make focused by default
        lightweightDialog.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        lightweightDialog.pseudoClassStateChanged(ACTIVE_PSEUDO_CLASS, true);
    }

    /**
     * @return all open lightweight dialogs from all {@link Stage}s
     */
    public static ObservableList<LightweightDialog> getOpenDialogs() {
        ObservableList<LightweightDialog> clone = FXCollections.observableArrayList();
        OPEN_DIALOGS.values().forEach(clone::addAll);
        return FXCollections.unmodifiableObservableList(clone);
    }

    /**
     * @param stage the {@link Stage} to query the open lightweight dialogs for
     * @return all open lightweight dialogs from the specified {@link Stage}
     */
    public static ObservableList<LightweightDialog> getOpenDialogs(Stage stage) {
        if (OPEN_DIALOGS.get(stage) == null)
            return FXCollections.unmodifiableObservableList(FXCollections.observableArrayList());
        return FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(OPEN_DIALOGS.get(stage)));
    }

    /*
     **************************************************************************
     *
     * Public API
     *
     **************************************************************************/

    public boolean isModal() {
        return modal;
    }

    public void setModal(boolean modal) {
        this.modal = modal;
    }

    public ObservableList<String> getStylesheets() {
        return scene.getStylesheets();
    }

    public void setEffect(Effect e) {
        this.effect = e;
    }

    public StringProperty titleProperty() {
        if (title == null)
            title = new SimpleStringProperty(this, "title");
        return title;
    }

    public BooleanProperty resizableProperty() {
        if (resizable == null)
            resizable = new SimpleBooleanProperty(this, "resizable", false);
        return resizable;
    }

    public boolean isResizable() {
        return resizableProperty().get();
    }

    public void setResizable(boolean resizable) {
        resizableProperty().set(resizable);
    }

    public BooleanProperty draggableProperty() {
        if (draggable == null)
            draggable = new SimpleBooleanProperty(this, "draggable", false);
        return draggable;
    }

    public boolean isDraggable() {
        return draggableProperty().get();
    }

    public void setDraggable(boolean draggable) {
        draggableProperty().set(draggable);
    }

    public BooleanProperty chromeVisibleProperty() {
        if (chromeVisible == null)
            chromeVisible = new SimpleBooleanProperty(this, "chromeVisible", true);
        return chromeVisible;
    }

    public boolean isChromeVisible() {
        return chromeVisibleProperty().get();
    }

    public void setChromeVisible(boolean chromeVisible) {
        chromeVisibleProperty().set(chromeVisible);
    }

    public BooleanProperty useHorizontalBackgroundProperty() {
        if (useHorizontalBackground == null)
            useHorizontalBackground = new SimpleBooleanProperty(this, "useHorizontalBackground", true);
        return useHorizontalBackground;
    }

    public boolean isUseHorizontalBackground() {
        return useHorizontalBackgroundProperty().get();
    }

    public void setUseHorizontalBackground(boolean useHorizontalBackground) {
        useHorizontalBackgroundProperty().set(useHorizontalBackground);
    }

    public BooleanProperty useTransitionProperty() {
        if (useTransition == null)
            useTransition = new SimpleBooleanProperty(this, "useTransition", false);
        return useTransition;
    }

    public boolean isUseTransition() {
        return useTransitionProperty().get();
    }

    public void setUseTransition(boolean useTransition) {
        useTransitionProperty().set(useTransition);
    }

    public ObjectProperty<Duration> transitionDurationProperty() {
        if (transitionDuration == null)
            transitionDuration = new SimpleObjectProperty<>(this, "transitionDuration", Duration.ZERO);
        return transitionDuration;
    }

    public Duration getTransitionDuration() {
        return transitionDurationProperty().get();
    }

    public void setTransitionDuration(Duration duration) {
        transitionDurationProperty().set(duration);
    }

    public Window getWindow() {
        return scene.getWindow();
    }

    public Node getRoot() {
        return lightweightDialog;
    }

    public ReadOnlyDoubleProperty widthProperty() {
        return lightweightDialog.widthProperty();
    }

    public ReadOnlyDoubleProperty heightProperty() {
        return lightweightDialog.heightProperty();
    }

    public BooleanProperty focusedProperty() {
        if (focused == null)
            focused = new SimpleBooleanProperty(this, "focused", true);
        return focused;
    }

    public void setContentPane(Pane pane) {
        root.setCenter(pane);
        BorderPane.setAlignment(pane, Pos.CENTER);
    }

    public void sizeToScene() {
        // no-op: This isn't needed when there is not stage...
    }

    boolean isIconified() {
        return false;
    }

    void setIconified(boolean iconified) {
        // no-op: We don't want to iconify lightweight dialogs
    }

    public void setIconifiable(boolean iconifiable) {
        // no-op: We don't want to iconify lightweight dialogs
    }

    public void setClosable(boolean closable) {
        closeButton.setVisible(closable);
    }

    public Optional<ButtonType> show() {

        if (owner != null && owner.getParent() != null) {
            showInParent();
        } else if (scene != null) {
            showInScene();
        }

        addToOpenDialogs();

        if (isModal()) {
            // This forces the lightweight dialog to be modal
            // Object lock = owner != null ? owner : scene;
            // XXX get the return value (see below) and return it, if we want such behavior
            ButtonType type = (ButtonType) Toolkit.getToolkit().enterNestedEventLoop(myLock);
            return Optional.ofNullable(type);
        }

        return Optional.ofNullable(null);
    }

    public void hide() {

        Transition transition = null;
        if (owner != null) {
            transition = hideInParent();
        } else if (scene != null) {
            transition = hideInScene();
        }
        if (transition != null)
            transition.playFromStart();

        if (isModal()) {
            // stop the lightweight dialog from being modal (i.e. restart the
            // execution after it paused with the dialog being shown)
            // Object lock = owner != null ? owner : scene;
            // XXX if we want a return value, put it here
            Toolkit.getToolkit().exitNestedEventLoop(myLock, result.get());
        }

        removeFromOpenDialogs();
    }

    private void addToOpenDialogs() {
        ObservableList<LightweightDialog> list = OPEN_DIALOGS.get(myStage);
        if (list == null) {
            list = FXCollections.observableArrayList();
            OPEN_DIALOGS.put(myStage, list);
        }
        list.add(this);
    }

    private void removeFromOpenDialogs() {
        ObservableList<LightweightDialog> list = OPEN_DIALOGS.get(myStage);
        if (list == null) {
            list = FXCollections.observableArrayList();
            OPEN_DIALOGS.put(myStage, list);
        }
        list.remove(this);
    }

    /*
     **************************************************************************
     *
     * Private implementation
     *
     **************************************************************************/

    protected final void init(String title, boolean useCustomChrome, ButtonType... buttonTypes) {
        titleProperty().set(title);

        resizableProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable valueModel) {

                if (maxButton != null) {
                    maxButton.setVisible(resizableProperty().get());

                    if (resizableProperty().get()) {
                        if (!windowBtns.getChildren().contains(maxButton)) {
                            windowBtns.getChildren().add(1, maxButton);
                        }
                    } else {
                        windowBtns.getChildren().remove(maxButton);
                    }
                }
            }
        });

        root = new BorderPane();
        if (buttonTypes != null && buttonTypes.length > 0) {
            final ButtonBar buttonBar = new ButtonBar();
            Arrays.asList(buttonTypes).stream().forEach(type -> {
                Button button = new Button(type.getText());
                button.getStyleClass().add("secondary");
                ButtonBar.setButtonData(button, type.getButtonData());
                buttonBar.getButtons().add(button);
                button.setOnAction(buttonEvent -> {
                    result.set(type);
                    LightweightDialog.this.hide();
                });
            });
            BorderPane.setMargin(buttonBar, new Insets(5.0, 0.0, 0.0, 0.0));
            root.setBottom(buttonBar);
        }

        // // we use different CSS to more closely mimic the underlying platform
        // final String platform = Utils.isMac() ? "mac" :
        // Utils.isUnix() ? "unix" :
        // Utils.isWindows() ? "windows" :
        // "";

        // *** The rest is for adding window decorations ***
        lightweightDialog = new StackPane() {
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                if (resizeCorner != null) {
                    resizeCorner.relocate(getWidth() - 20, getHeight() - 20);
                }
            }
        };
        lightweightDialog.getChildren().add(root);
        lightweightDialog.getStyleClass().addAll("dialog", "decorated-root", org.controlsfx.tools.Platform.getCurrent().getPlatformId());

        resizeCorner = new Rectangle(10, 10);
        resizeCorner.getStyleClass().add("window-resize-corner");
        resizeCorner.setManaged(false);

        resizeCorner.visibleProperty().bind(resizableProperty());

        if (!useCustomChrome) {
            return;
        }

        focusedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable valueModel) {
                boolean active = ((ReadOnlyBooleanProperty) valueModel).get();
                lightweightDialog.pseudoClassStateChanged(ACTIVE_PSEUDO_CLASS, active);
            }
        });

        toolBar = new ToolBar();
        toolBar.getStyleClass().add("window-header");
        toolBar.setPrefHeight(HEADER_HEIGHT);
        toolBar.setMinHeight(HEADER_HEIGHT);
        toolBar.setMaxHeight(HEADER_HEIGHT);

        titleLabel = new Label();
        titleLabel.setMaxHeight(Double.MAX_VALUE);
        titleLabel.getStyleClass().add("window-title");
        titleLabel.setText(titleProperty().get());

        titleProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable valueModel) {
                titleLabel.setText(titleProperty().get());
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // add close min max
        closeButton = new WindowButton("close");
        closeButton.setFocusTraversable(false);
        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                LightweightDialog.this.hide();
            }
        });
        minButton = new WindowButton("minimize");
        minButton.setFocusTraversable(false);
        minButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setIconified(isIconified());
            }
        });

        maxButton = new WindowButton("maximize");
        maxButton.setFocusTraversable(false);

        windowBtns = new HBox(3);
        windowBtns.getStyleClass().add("window-buttons");
        windowBtns.getChildren().addAll(minButton, maxButton, closeButton);

        toolBar.getItems().addAll(titleLabel, spacer, windowBtns);
        root.setTop(toolBar);

        toolBar.visibleProperty().bind(chromeVisibleProperty());
        toolBar.managedProperty().bind(chromeVisibleProperty());

        chromeVisibleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || !newValue) {
                lightweightDialog.getStyleClass().add("borderless");
            } else {
                lightweightDialog.getStyleClass().remove("borderless");
            }
        });

        lightweightDialog.getChildren().add(resizeCorner);
    }

    private Transition hideInScene() {

        ParallelTransition parallel = new ParallelTransition();
        parallel.setAutoReverse(false);
        parallel.setCycleCount(1);
        FadeTransition fadeContent = new FadeTransition(getTransitionDuration(), lightweightDialog);
        fadeContent.setFromValue(1);
        fadeContent.setToValue(0);
        parallel.getChildren().add(fadeContent);
        if (effect == null && opaqueLayer != null) {
            FadeTransition fadeOpacity = new FadeTransition(getTransitionDuration(), opaqueLayer);
            fadeOpacity.setFromValue(1);
            fadeOpacity.setToValue(0);
            parallel.getChildren().add(fadeOpacity);
        }
        if (effect == null && horizontalBackground != null) {
            FadeTransition fadeBackground = new FadeTransition(getTransitionDuration(), horizontalBackground);
            fadeBackground.setFromValue(1);
            fadeBackground.setToValue(0);
            parallel.getChildren().add(fadeBackground);
        }

        parallel.setOnFinished(event -> {
            // remove the opaque layer behind the dialog, if it was used
            if (opaqueLayer != null) {
                opaqueLayer.setVisible(false);
            }

            // reset the effect on the parent
            originalParent.setEffect(tempEffect);

            // hide the dialog
            lightweightDialog.setVisible(false);

            // reset the scene root
            dialogStack.getChildren().remove(originalParent);
            dialogStack.toBack();
            originalParent.getStyleClass().remove("root");

            scene.setRoot(originalParent);
        });

        return parallel;
    }

    private Transition hideInParent() {

        ParallelTransition parallel = new ParallelTransition();
        parallel.setAutoReverse(false);
        parallel.setCycleCount(1);
        FadeTransition fadeContent = new FadeTransition(getTransitionDuration(), lightweightDialog);
        fadeContent.setFromValue(1);
        fadeContent.setToValue(0);
        parallel.getChildren().add(fadeContent);
        if (effect == null && opaqueLayer != null) {
            FadeTransition fadeOpacity = new FadeTransition(getTransitionDuration(), opaqueLayer);
            fadeOpacity.setFromValue(1);
            fadeOpacity.setToValue(0);
            parallel.getChildren().add(fadeOpacity);
        }
        if (effect == null && horizontalBackground != null) {
            FadeTransition fadeBackground = new FadeTransition(getTransitionDuration(), horizontalBackground);
            fadeBackground.setFromValue(1);
            fadeBackground.setToValue(0);
            parallel.getChildren().add(fadeBackground);
        }

        parallel.setOnFinished(event -> {
            // remove the opaque layer behind the dialog, if it was used
            if (opaqueLayer != null) {
                opaqueLayer.setVisible(false);
            }

            // reset the effect on the parent
            if (originalParent != null) {
                originalParent.setEffect(tempEffect);
            }

            // hide the dialog
            lightweightDialog.setVisible(false);

            dialogStack.toBack();

            // reset the scenegraph
            getChildren(owner.getParent()).setAll(owner);

            dialogStack = null;
        });

        return parallel;
    }

    private void showInScene() {
        installCSSInScene();

        // modify scene root to install opaque layer and the dialog
        originalParent = scene.getRoot();
        Transition transition = buildDialogStack(originalParent);

        lightweightDialog.setVisible(true);
        scene.setRoot(dialogStack);
        lightweightDialog.requestFocus();

        transition.playFromStart();
    }

    private void showInParent() {
        installCSSInScene();

        ObservableList<Node> ownerParentChildren = getChildren(owner.getParent());

        // we've got the children list, now we need to insert a temporary
        // layout container holding our dialogs and opaque layer / effect
        // in place of the owner (the owner will become a child of the dialog
        // stack)
        int ownerPos = ownerParentChildren.indexOf(owner);
        ownerParentChildren.remove(ownerPos);
        Transition transition = buildDialogStack(owner);
        ownerParentChildren.add(ownerPos, dialogStack);

        lightweightDialog.setVisible(true);
        lightweightDialog.requestFocus();
        dialogStack.toFront();

        transition.playFromStart();
    }

    private void installCSSInScene() {

        String dialogsCssUrl = DIALOGS_CSS_URL.toExternalForm();
        if (scene != null) {
            // install CSS
            if (!scene.getStylesheets().contains(dialogsCssUrl)) {
                scene.getStylesheets().addAll(dialogsCssUrl);
            }
        } else if (owner != null) {
            Scene _scene = owner.getScene();
            if (_scene != null) {
                // install CSS
                if (!scene.getStylesheets().contains(dialogsCssUrl)) {
                    _scene.getStylesheets().addAll(dialogsCssUrl);
                }
            }
        }
    }

    private Transition buildDialogStack(final Node parent) {

        for (Node node : parent.lookupAll(".lightweight-dialog-stack")) {
            node.toBack();
        }

        dialogStack = new Pane(lightweightDialog) {
            private boolean isFirstRun = true;

            protected void layoutChildren() {
                final double w = getOverlayWidth();
                final double h = getOverlayHeight();

                final double x = getOverlayX();
                final double y = getOverlayY();

                if (parent != null) {
                    parent.resizeRelocate(x, y, w, h);
                }

                if (opaqueLayer != null) {
                    opaqueLayer.resizeRelocate(x, y, w, h);
                }

                final double dialogWidth = lightweightDialog.prefWidth(-1);
                final double dialogHeight = lightweightDialog.prefHeight(-1);

                lightweightDialog.resize(snapSize(dialogWidth), snapSize(dialogHeight));

                // hacky, but we only want to position the dialog the first time
                // it is laid out - after that the only way it should move is if
                // the user moves it.
                if (isFirstRun) {
                    isFirstRun = false;

                    double dialogX = lightweightDialog.getLayoutX();
                    dialogX = dialogX == 0.0 ? w / 2.0 - dialogWidth / 2.0 : dialogX;

                    double dialogY = lightweightDialog.getLayoutY();
                    dialogY = dialogY == 0.0 ? h / 2.0 - dialogHeight / 2.0 : dialogY;

                    lightweightDialog.relocate(snapPosition(dialogX), snapPosition(dialogY));

                    if (horizontalBackground != null) {
                        horizontalBackground.resizeRelocate(x, dialogY, w, dialogHeight);
                    }
                }
            }
        };
        dialogStack.getStyleClass().add("lightweight-dialog-stack");
        dialogStack.setManaged(true);
        dialogStack.toFront();

        if (parent != null) {
            dialogStack.getChildren().add(0, parent);

            // copy in layout properties, etc, so that the dialogStack displays
            // properly in (hopefully) whatever layout the owner node is in
            dialogStack.getProperties().putAll(parent.getProperties());
        }

        if (effect == null) {

            // opaque layer
            opaqueLayer = new Region();
            opaqueLayer.getStyleClass().add("lightweight-dialog-background");
            dialogStack.getChildren().add(parent == null ? 0 : 1, opaqueLayer);

            if (isUseHorizontalBackground()) {
                horizontalBackground = new Region();
                horizontalBackground.getStyleClass().addAll("lightweight-dialog-background", "horizontal");
                dialogStack.getChildren().add(parent == null ? 1 : 2, horizontalBackground);
            }

            if (parent.lookup(".lightweight-dialog-background") != null) {
                dialogStack.getChildren().remove(opaqueLayer);
            }

        } else {
            if (parent != null) {
                tempEffect = parent.getEffect();
                parent.setEffect(effect);
            }
        }

        ParallelTransition parallel = new ParallelTransition();
        parallel.setAutoReverse(false);
        parallel.setCycleCount(1);
        FadeTransition fadeContent = new FadeTransition(getTransitionDuration(), lightweightDialog);
        fadeContent.setFromValue(0);
        fadeContent.setToValue(1);
        parallel.getChildren().add(fadeContent);
        if (effect == null && opaqueLayer != null) {
            FadeTransition fadeOpacity = new FadeTransition(getTransitionDuration(), opaqueLayer);
            fadeOpacity.setFromValue(0);
            fadeOpacity.setToValue(1);
            parallel.getChildren().add(fadeOpacity);
        }
        if (effect == null && horizontalBackground != null) {
            FadeTransition fadeBackground = new FadeTransition(getTransitionDuration(), horizontalBackground);
            fadeBackground.setFromValue(0);
            fadeBackground.setToValue(1);
            parallel.getChildren().add(fadeBackground);
        }
        return parallel;
    }

    private double getOverlayWidth() {
        if (owner != null) {
            return owner.getLayoutBounds().getWidth();
        } else if (scene != null) {
            return scene.getWidth();
        }

        return 0;
    }

    private double getOverlayHeight() {
        if (owner != null) {
            return owner.getLayoutBounds().getHeight();
        } else if (scene != null) {
            return scene.getHeight();
        }

        return 0;
    }

    private double getOverlayX() {
        return 0;
    }

    private double getOverlayY() {
        return 0;
    }

    private Parent getFirstParent(Node n) {
        if (n == null)
            return null;
        return n instanceof Parent ? (Parent) n : getFirstParent(n.getParent());
    }

    @SuppressWarnings("unchecked")
    private ObservableList<Node> getChildren(Parent p) {
        ObservableList<Node> children = null;

        try {
            Method getChildrenMethod = Parent.class.getDeclaredMethod("getChildren");

            if (getChildrenMethod != null) {
                if (!getChildrenMethod.isAccessible()) {
                    getChildrenMethod.setAccessible(true);
                }
                children = (ObservableList<Node>) getChildrenMethod.invoke(p);
            } else {
                // uh oh, trouble
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return children;
    }

    /*
     ***************************************************************************
     *
     * Support Classes
     *
     **************************************************************************/

    private static class WindowButton extends Button {
        WindowButton(String name) {
            getStyleClass().setAll("window-button");
            getStyleClass().add("window-" + name + "-button");
            StackPane graphic = new StackPane();
            graphic.getStyleClass().setAll("graphic");
            setGraphic(graphic);
            setMinSize(17, 17);
            setPrefSize(17, 17);
        }
    }
}