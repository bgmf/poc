package eu.dzim.shared.fx.util;

import eu.dzim.shared.disposable.Disposable;
import eu.dzim.shared.disposable.DisposableHolder;
import javafx.animation.*;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.Property;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseController implements Disposable {

    private static final Logger LOG = LogManager.getLogger(BaseController.class);

    protected void bindContainerVisibility(Pane container, BooleanBinding binding) {
        container.visibleProperty().unbind();
        container.managedProperty().unbind();
        container.visibleProperty().bind(binding);
        container.managedProperty().bind(binding);
    }

    protected <T> void replaceContent(Parent container, Node node, Duration duration, DisposableHolder disposableHolder, boolean dispose,
            boolean triggerPropertyChange, Property<T> propertyToChange, T newPropertyValue) {

        ParallelTransition parallel = new ParallelTransition();
        parallel.setAutoReverse(false);
        parallel.setCycleCount(1);

        List<Node> oldNodes = new ArrayList<>();
        for (Node child : container.getChildrenUnmodifiable()) {
            // ignore the node to show, if it is already present
            if (child == node)
                continue;
            FadeTransition fade = new FadeTransition(duration, child);
            fade.setToValue(0);
            fade.setOnFinished(event -> {
                child.setVisible(false);
                // OLD: child.setManaged(false);
                // NEW: instead removing and not letting it be managed, put it to the back of the stack
                child.toBack();
            });
            parallel.getChildren().add(fade);
            oldNodes.add(child);
        }

        if (container instanceof Pane && !((Pane) container).getChildren().contains(node))
            ((Pane) container).getChildren().add(node);
        else if (container instanceof ScrollPane && !((ScrollPane) container).getContent().equals(node))
            ((ScrollPane) container).setContent(node);

        // made them visible again, if it was previously hidden
        if (!node.isVisible())
            node.setVisible(true);

        // NEW: bring the node to show to the front
        node.toFront();

        // set the opacity to zero, to enable fade-in
        node.setOpacity(0);

        FadeTransition fade = new FadeTransition(duration, node);
        fade.setToValue(1);
        parallel.getChildren().add(fade);

        parallel.setOnFinished(event -> {
            if (dispose)
                oldNodes.stream().forEach(t -> disposeDisposables(disposableHolder, t));
            if (container instanceof Pane) {
                // OLD: ((Pane) container).getChildren().removeAll(oldNodes);
            }
            if (triggerPropertyChange) {
                // applicationModel.setShowBusyOverlay(false);
                propertyToChange.setValue(newPropertyValue);
            }
        });

        parallel.play();
    }

    private void disposeDisposables(DisposableHolder disposableHolder, Node node) {
        if (disposableHolder == null || node == null || node.getUserData() == null || !(node.getUserData() instanceof Disposable))
            return;
        ((Disposable) node.getUserData()).dispose();
        if (!disposableHolder.getDisposables().remove(node.getUserData())) {
            LOG.warn("Could not remove the Disposable {} from the DisposableHolder.", node.getUserData());
        }
    }

    /**
     * Override this method, if you want to call {@link Pane#setUserData(Object)} - the {@link #showPane(Pane, boolean, Duration)} method uses the
     * actual height values for the {@link Pane}s by reading them from the user data.
     */
    @Deprecated
    protected void setupShowPane() {
    }

    /**
     * Method to either show or hide a pane by resizing, translating, scaling and fading it in parallel.
     *
     * @param pane     the {@link Pane} to either show or hide
     * @param show     <code>true</code>, if you want to show it, <code>false</code> otherwise
     * @param duration the {@link Duration} for this animation
     */
    @Deprecated
    protected void showPane(Pane pane, boolean show, Duration duration) {
        if (show) {
            // pane.setVisible(true);
            pane.setManaged(true);
        }
        // hide on begin - otherwise we have ugly overlapping nodes
        else {
            pane.setVisible(false);
        }
        ResizeHeightTransition height = new ResizeHeightTransition(duration, pane, show ? (double) pane.getUserData() : 0.0);
        height.play();
        TranslateTransition translate = new TranslateTransition(duration, pane);
        translate.setByY(show ? (double) pane.getUserData() : (double) pane.getUserData() * -1.0);
        ScaleTransition scale = new ScaleTransition(duration, pane);
        scale.setToY(show ? 1.0 : 0.0);
        // FadeTransition fade = new FadeTransition(duration, pane);
        // fade.setToValue(show ? 1.0 : 0.0);
        ParallelTransition parallel = new ParallelTransition(height, translate, scale);
        parallel.setAutoReverse(false);
        parallel.setCycleCount(1);
        parallel.play();
        parallel.setOnFinished(event -> {
            if (!show) {
                pane.setVisible(false);
                pane.setManaged(false);
            }
            // show, when done - otherwise we have ugly overlapping nodes
            else {
                pane.setVisible(true);
                pane.setMinHeight(Pane.USE_COMPUTED_SIZE);
                pane.setMaxHeight(Pane.USE_COMPUTED_SIZE);
            }
        });
    }

    @Deprecated
    protected void rotateButtonWithPaneAsUserData(ActionEvent event, Duration duration) {
        Pane userData = null;
        boolean show = false;
        if (event.getSource() instanceof CheckBox) {
            userData = (Pane) ((CheckBox) event.getSource()).getUserData();
            show = ((CheckBox) event.getSource()).isSelected();
            userData.setVisible(show);
            userData.setManaged(show);
            return;
        } else if (event.getSource() instanceof Button) {
            Button button = (Button) event.getSource();
            if (button.getUserData() == null || !(button.getUserData() instanceof Pane))
                return;
            double angle = 0.0;
            double angle2 = 0.0;
            Node toRotate = button.getGraphic().lookup("#to-rotate-90");
            if (toRotate != null) {
                angle = -90.0;
            } else {
                angle = 180.0;
                toRotate = button;
            }
            Node toRotate180 = button.getGraphic().lookup("#to-rotate-180");
            if (toRotate180 != null) {
                angle2 = -180.0;
            }
            RotateTransition rotate = new RotateTransition(duration, toRotate);
            rotate.setToAngle(Math.abs(toRotate.getRotate()) > 0.0 ? 0.0 : angle);
            ParallelTransition parallel = new ParallelTransition(rotate);
            if (toRotate180 != null) {
                RotateTransition rotate180 = new RotateTransition(duration, toRotate180);
                rotate180.setToAngle(Math.abs(toRotate180.getRotate()) > 0.0 ? 0.0 : angle2);
                parallel.getChildren().add(rotate180);
            }
            parallel.setAutoReverse(false);
            parallel.setCycleCount(1);
            parallel.play();

            userData = (Pane) button.getUserData();
            show = Math.abs(toRotate.getRotate()) > 0;
            setupShowPane();
            showPane(userData, show, duration);
        } else {
            return;
        }
    }

    protected void handleContentScrolling(ScrollPane scrollPane, ScrollEvent event, boolean consumeEvent) {
        handleContentScrolling(scrollPane, event, consumeEvent, 1, 1);
    }

    protected void handleContentScrolling(ScrollPane scrollPane, ScrollEvent event, boolean consumeEvent, double vhSpeedModifier) {
        handleContentScrolling(scrollPane, event, consumeEvent, vhSpeedModifier, vhSpeedModifier);
    }

    protected void handleContentScrolling(ScrollPane scrollPane, ScrollEvent event, boolean consumeEvent, double vSpeedMultiplier,
            double hSpeedMultiplier) {
        if (event.getDeltaY() != 0)
            handleVerticalScrolling(scrollPane, event, vSpeedMultiplier);
        if (event.getDeltaX() != 0)
            handleHorizontalScrolling(scrollPane, event, hSpeedMultiplier);
        if (consumeEvent)
            event.consume();
    }

    private void handleHorizontalScrolling(ScrollPane scrollPane, ScrollEvent event, double hSpeedMultiplier) {
        // *x to make the scrolling a bit faster
        double deltaX = event.getDeltaX() * Math.abs(hSpeedMultiplier);
        double width = scrollPane.getContent().getBoundsInLocal().getWidth();
        double hvalue = scrollPane.getHvalue();
        // deltaY/width to make the scrolling equally fast regardless of the actual width of the component
        scrollPane.setHvalue(hvalue + -deltaX / width);
    }

    private void handleVerticalScrolling(ScrollPane scrollPane, ScrollEvent event, double vSpeedMultiplier) {
        // *x to make the scrolling a bit faster
        double deltaY = event.getDeltaY() * Math.abs(vSpeedMultiplier);
        double height = scrollPane.getContent().getBoundsInLocal().getHeight();
        double vvalue = scrollPane.getVvalue();
        // deltaX/height to make the scrolling equally fast regardless of the actual height of the component
        scrollPane.setVvalue(vvalue + -deltaY / height);
    }

    @Override
    public void dispose() {
        LOG.debug("Disposing class: " + this.getClass().getSimpleName());
    }
}
