package eu.dzim.shared.fx.ui.controller;

import eu.dzim.shared.resource.Resource;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class BreadcrumbController {

    private static final Logger LOG = LogManager.getLogger(BreadcrumbController.class);

    private static final int ARROW_WIDTH = 10;
    private static final int ARROW_HEIGHT = 50;

    @FXML private ToggleGroup breadcrumpToggleGroup;

    @FXML private HBox container;
    @FXML private Button back;
    @FXML private Label spacerLeft;
    @FXML private HBox breadcrumbBox;
    @FXML private Button forward;
    @FXML private Label spacerRight;

    private BooleanProperty showButtons = new SimpleBooleanProperty(true);
    private BooleanProperty showSpacer = new SimpleBooleanProperty(false);
    private BooleanProperty stretchBreadcrumbBox = new SimpleBooleanProperty(true);

    private ObservableList<ToggleButton> breadcrumbs = FXCollections.observableArrayList();

    public BreadcrumbController() {
        // sonar
    }

    public ToggleGroup getToggleGroup() {
        return breadcrumpToggleGroup;
    }

    public final BooleanProperty showButtonsProperty() {
        return this.showButtons;
    }

    public final boolean isShowButtons() {
        return this.showButtonsProperty().get();
    }

    public final void setShowButtons(final boolean showButtons) {
        this.showButtonsProperty().set(showButtons);
    }

    public final BooleanProperty showSpacerProperty() {
        return this.showSpacer;
    }

    public final boolean isShowSpacer() {
        return this.showSpacerProperty().get();
    }

    public final void setShowSpacer(final boolean showSpacer) {
        this.showSpacerProperty().set(showSpacer);
    }

    public final BooleanProperty stretchBreadcrumpBoxProperty() {
        return this.stretchBreadcrumbBox;
    }

    public final boolean isStretchBreadcrumbBox() {
        return this.stretchBreadcrumpBoxProperty().get();
    }

    public final void setStretchBreadcrumbBox(final boolean stretchBreadcrumbBox) {
        this.stretchBreadcrumpBoxProperty().set(stretchBreadcrumbBox);
    }

    public ObservableList<ToggleButton> getBreadcrumbs() {
        return breadcrumbs;
    }

    @FXML
    protected void initialize() {

        back.visibleProperty().bind(showButtons);
        back.managedProperty().bind(showButtons);
        forward.visibleProperty().bind(showButtons);
        forward.managedProperty().bind(showButtons);

        spacerLeft.visibleProperty().bind(showSpacer);
        spacerLeft.managedProperty().bind(showSpacer);
        spacerRight.visibleProperty().bind(showSpacer);
        spacerRight.managedProperty().bind(showSpacer);

        stretchBreadcrumbBox.addListener(this::handleStretchBbreadcrumbBoxChange);
        breadcrumbs.addListener(this::handleBreadcrumbsChange);
    }

    private void handleStretchBbreadcrumbBoxChange(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (newValue == null)
            return;
        if (newValue)
            HBox.setHgrow(breadcrumbBox, Priority.ALWAYS);
        else
            HBox.setHgrow(breadcrumbBox, Priority.NEVER);
    }

    private void handleBreadcrumbsChange(javafx.collections.ListChangeListener.Change<? extends ToggleButton> change) {
        while (change.next()) {
            if (change.getAddedSize() > 0)
                handleBreadcrumbsAdded(change.getAddedSubList());
            if (change.getRemovedSize() > 0)
                handleBreadcrumbsRemoved(change.getRemoved());
        }
    }

    private void handleBreadcrumbsAdded(List<? extends ToggleButton> added) {
        added.forEach(this::addBreadcrumb);
    }

    private void handleBreadcrumbsRemoved(List<? extends ToggleButton> removed) {
        breadcrumpToggleGroup.getToggles().removeAll(removed);
        breadcrumbBox.getChildren().removeAll(removed);
    }

    public ToggleButton addBreadcrumb(Resource resource, String text, Node graphic, ContentDisplay contentDisplay,
            EventHandler<ActionEvent> onAction) {

        ToggleButton button = new ToggleButton();
        button.setContentDisplay(contentDisplay);
        button.setGraphic(graphic);
        Pair<Resource, String> pair = new Pair<>(resource, text);
        button.setUserData(pair);
        String buttonText = getButtonText(button);
        button.setText(buttonText);
        button.setToggleGroup(breadcrumpToggleGroup);

        button.setOnAction(onAction);

        if (ContentDisplay.GRAPHIC_ONLY != contentDisplay && buttonText == null) {
            LOG.warn("The button setup is incomplete. Not adding button {}: no text", text);
            return null;
        }
        if (ContentDisplay.GRAPHIC_ONLY == contentDisplay && graphic == null) {
            LOG.warn("The button setup is incomplete. Not adding button {}: no graphic node", text);
            return null;
        }

        addBreadcrumb(button);

        return button;
    }

    public ToggleButton addBreadcrumb(Resource resource, String text, EventHandler<ActionEvent> onAction) {
        return addBreadcrumb(resource, text, null, ContentDisplay.TEXT_ONLY, onAction);
    }

    public ToggleButton addBreadcrumb(Node graphic, EventHandler<ActionEvent> onAction) {
        return addBreadcrumb(null, null, graphic, ContentDisplay.GRAPHIC_ONLY, onAction);
    }

    @SuppressWarnings("unchecked")
    private String getButtonText(ToggleButton button) {

        if (button == null)
            return null;

        Pair<Resource, String> pair = (Pair<Resource, String>) button.getUserData();
        if (pair == null)
            return null;

        String buttonText = null;
        if (pair.getKey() != null && pair.getValue() != null)
            buttonText = pair.getKey().getGuaranteedString(pair.getValue());
        if (buttonText == null && pair.getValue() != null)
            buttonText = pair.getValue();

        if (buttonText == null) {
            return "";
        } else {
            return buttonText;
        }
    }

    protected void addBreadcrumb(ToggleButton button) {
        button.getStyleClass().add("breadcrumb");
        if (breadcrumbBox.getChildren().size() == 0)
            button.getStyleClass().add("first");
        // setClippingOn(button);
        button.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> setClippingOn(button));
        breadcrumbBox.getChildren().add(button);
    }

    private void setClippingOn(ToggleButton button) {

        int index = -1;

        for (int i = 0; i < breadcrumbBox.getChildren().size(); i++) {
            if (breadcrumbBox.getChildren().get(i) == button) {
                index = i;
                break;
            }
        }

        if (index == -1)
            return;

        button.setTranslateX(index * 8 * -1.0);

        // build the following shape
        // --------
        // \ \
        // / /
        // --------
        Path path = new Path();

        // begin in the upper left corner
        MoveTo e1 = new MoveTo(0, 0);

        // draw a horizontal line that defines the width of the shape
        HLineTo e2 = new HLineTo();
        // bind the width of the shape to the width of the button
        e2.xProperty().bind(button.widthProperty().subtract(ARROW_WIDTH));

        // draw upper part of right arrow
        LineTo e3 = new LineTo();
        // the x endpoint of this line depends on the x property of line e2
        e3.xProperty().bind(e2.xProperty().add(ARROW_WIDTH));
        e3.setY(button.getHeight() / 2.0); // ARROW_HEIGHT

        // draw lower part of right arrow
        LineTo e4 = new LineTo();
        // the x endpoint of this line depends on the x property of line e2
        e4.xProperty().bind(e2.xProperty());
        e4.setY(button.getHeight()); // ARROW_HEIGHT

        // draw lower horizontal line
        HLineTo e5 = new HLineTo(0);

        // draw lower part of left arrow
        LineTo e6 = new LineTo(ARROW_WIDTH, button.getHeight() / 2.0); // ARROW_HEIGHT

        // close path
        ClosePath e7 = new ClosePath();

        if (button.getStyleClass().contains("first"))
            path.getElements().addAll(e1, e2, e3, e4, e5, e7);
        else
            path.getElements().addAll(e1, e2, e3, e4, e5, e6, e7);
        // this is a dummy color to fill the shape, it won't be visible
        path.setFill(Color.BLACK);

        // set path as button shape
        button.setClip(path);
    }

    @SuppressWarnings("unused")
    private void setClippingNodeOn(Button pane) {

        boolean arrow = true;

        // build the following shape
        // --------
        // | \
        // | /
        // | |
        // --------
        Path path = new Path();

        // begin in the upper left corner
        MoveTo e1 = new MoveTo(0, 0);

        // draw a horizontal line that defines the width of the shape
        HLineTo e2 = new HLineTo();
        // bind the width of the shape to the width of the button
        e2.xProperty().bind(pane.widthProperty().subtract(ARROW_WIDTH + 10));

        // draw upper part of right arrow
        LineTo e3 = new LineTo();
        // the x endpoint of this line depends on the x property of line e2
        if (arrow)
            e3.xProperty().bind(e2.xProperty().add(ARROW_WIDTH));
        else
            e3.xProperty().bind(e2.xProperty());
        e3.setY(ARROW_HEIGHT / 2.0);

        // draw lower part of right arrow
        LineTo e4 = new LineTo();
        // the x endpoint of this line depends on the x property of line e2
        e4.xProperty().bind(e2.xProperty());
        e4.setY(ARROW_HEIGHT);

        VLineTo e4_2 = new VLineTo();
        e4_2.yProperty().bind(pane.heightProperty());

        // draw lower horizontal line
        HLineTo e5 = new HLineTo(0);

        // draw lower part of left arrow
        // LineTo e6 = new LineTo(ARROW_WIDTH, ARROW_HEIGHT / 2.0);

        // close path
        ClosePath e7 = new ClosePath();

        path.getElements().addAll(e1, e2, e3, e4, e4_2, e5, e7);
        // this is a dummy color to fill the shape, it won't be visible
        path.setFill(Color.BLACK);

        DropShadow effect = new DropShadow(10.0, Color.web("#303030"));
        effect.setHeight(10);
        effect.setWidth(10);
        effect.setSpread(0.5);
        path.setEffect(effect);

        // set path as button shape
        pane.setClip(path);
    }

    @FXML
    public void handleBack(ActionEvent event) {
        Toggle toggle = breadcrumpToggleGroup.getSelectedToggle();
        if (toggle == null)
            return;
        int index = breadcrumbs.indexOf(toggle);
        if (index <= 0 || index >= breadcrumbs.size() - 1)
            return;
        breadcrumpToggleGroup.selectToggle(breadcrumbs.get(index - 1));
    }

    @FXML
    public void handleForward(ActionEvent event) {
        Toggle toggle = breadcrumpToggleGroup.getSelectedToggle();
        if (toggle == null)
            return;
        int index = breadcrumbs.indexOf(toggle);
        if (index <= 0 || index >= breadcrumbs.size() - 1)
            return;
        breadcrumpToggleGroup.selectToggle(breadcrumbs.get(index + 1));
    }
}
