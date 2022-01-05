package eu.dzim.tests.swing;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.*;
import javafx.util.Duration;

import javax.swing.*;
import java.util.function.Supplier;

public class SwingFxInteroptWithPreloader {
    public static void main(String[] args) {

        // init the ToolKit - can only be done once!
        new JFXPanel();
        // prevent implicit exit of the TK - we can't restart it in JFX 11
        Platform.setImplicitExit(false);

        // property to prevent, we are showing more than one "Application"
        final BooleanProperty appVisible = new SimpleBooleanProperty(false);
        // switch from Preloader to main App window
        final BooleanProperty showApp = new SimpleBooleanProperty(false);
        // keep in mind, we need to reset these properties!

        // init Swing frame
        JFrame f = new JFrame();
        // and add a Swing button to showcase this app
        JButton b = new JButton("Show 'Preloader'");
        b.setBounds(100, 100, 180, 40);

        f.add(b);

        // important -> kills the app
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(400, 280);
        f.setLayout(null);

        // show preloader on action event
        b.addActionListener(event -> {

            // check, whether the app is already visible -> could be done better, if we just remebemer the old app reference, I guess
            if (appVisible.get())
                return;

            // scene.setFill(Color.TRANSPARENT); root.setBackground(Background.EMPTY); primaryStage.initStyle(StageStyle.TRANSPARENT);

            // we can't directly interact with JavaFX from Swing, so we need a Platform.runLater()
            Platform.runLater(() -> {

                // preloader window
                final StageWrapper pre = new StageWrapper(StageStyle.TRANSPARENT, Modality.APPLICATION_MODAL, null, -1, -1, 320, 240, "Preloader",
                        false);
                pre.setSceneFill(Color.TRANSPARENT);

                // main window
                final StageWrapper app = new StageWrapper(StageStyle.DECORATED, Modality.APPLICATION_MODAL, null, -1, -1, 640, 480, "Application",
                        false);

                // the main window wrapper content creator
                app.setSceneContentSupplier(() -> {
                    BorderPane rootPane = new BorderPane();
                    rootPane.setCenter(new Label("Application"));
                    return rootPane;
                });

                // react on the showApp property, indicating, that the preloader is ready and the main window may appear
                // we keep the reference here, to remove it later on in the main window close event
                // otherwise we have many references on dead windows, we don't want that
                final ChangeListener<Boolean> showAppListener = (obs, o, n) -> {
                    if (n) {
                        pre.hide();
                        app.show();
                    }
                };

                // add the listener to the property
                showApp.addListener(showAppListener);
                // handle main window close request - clean everything up
                app.setOnCloseRequest(e -> {
                    showApp.removeListener(showAppListener); // remove the showApp listener
                    showApp.set(false); // reset showApp
                    appVisible.set(false); // reset appVisible
                });

                // preloader window creator combined with showing it... just for the simplicity
                pre.showContent(false, () -> {

                    BorderPane rootPane = new BorderPane();
                    rootPane.setCenter(new Label("Preloader"));

                    // handle layout changes:
                    // - set the preloader window position
                    // - init the showcase timeline, that will indicate, when the preloader is done
                    rootPane.layoutBoundsProperty().addListener((obs, o, n) -> {

                        if (n.getHeight() <= 10.0 && n.getWidth() <= 10.0)
                            return;

                        Screen primaryScreen = Screen.getPrimary();
                        Rectangle2D vb = primaryScreen.getVisualBounds();
                        double x = vb.getMinX() + (vb.getMaxX() - vb.getMinX()) / 2.0;
                        double y = (vb.getMaxY() - vb.getMinY()) / 2.0;

                        pre.setX(x - n.getWidth() / 2.0);
                        pre.setY(y - n.getHeight() / 2.0);

                        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(2500), ae -> showApp.set(true)));
                        timeline.play();
                    });

                    // don't forget to set the property, that the app is now visible
                    // (regardless of whether it's just the preloader or the main window)
                    appVisible.set(true);

                    return rootPane;
                });
            });
        });

        // show the Swing window
        f.setVisible(true);
    }
}

class StageWrapper extends Stage {

    private final double width;
    private final double height;

    private Paint sceneFill = Color.WHITE;
    private Supplier<Pane> sceneContentSupplier = BorderPane::new;

    public StageWrapper(StageStyle style, Modality modality, Window owner, double minWidth, double minHeight, double width, double height,
            String title, boolean alwaysOnTop) {

        super(style);

        initModality(modality);
        initOwner(owner);

        if (minWidth > 0)
            setMinWidth(minWidth);
        if (minHeight > 0)
            setMinHeight(minHeight);

        this.width = width;
        this.height = height;

        setTitle(title);
        setAlwaysOnTop(alwaysOnTop);
    }

    public void setSceneFill(Paint sceneFill) {
        this.sceneFill = sceneFill;
    }

    public void setSceneContentSupplier(Supplier<Pane> sceneContentSupplier) {
        this.sceneContentSupplier = sceneContentSupplier;
        createContent();
    }

    private void createContent() {
        Pane content = sceneContentSupplier.get();
        Scene scene = new Scene(content, width, height);
        scene.setFill(sceneFill);
        setScene(scene);
    }

    public void showContent(boolean showAndWait, Supplier<Pane> sceneContentSupplier) {

        this.sceneContentSupplier = sceneContentSupplier;

        createContent();

        if (showAndWait)
            showAndWait();
        else
            show();
    }
}
