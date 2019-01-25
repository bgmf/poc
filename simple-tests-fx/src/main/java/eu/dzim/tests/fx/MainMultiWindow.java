package eu.dzim.tests.fx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class MainMultiWindow extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    Stage primaryStage = null;

    @Override
    public void start(Stage stage) {

        this.primaryStage = stage;

        stage.setTitle("Main Window");
        stage.setMinWidth(640);
        stage.setMinHeight(480);

        Button open = new Button("Show Secondary Window");
        open.setOnAction(e -> {
            showSecondaryStage();
            e.consume();
        });

        BorderPane root = new BorderPane();
        root.setCenter(open);

        Scene scene = new Scene(root, 640, 480);

        stage.setScene(scene);
        stage.show();
    }

    private void showSecondaryStage() {

        /*
        Stage stage = new Stage(StageStyle.DECORATED);
        // WINDOW_MODAL = only modal to its owner window(s)
        // APPLICATION_MODAL = modal to every window - Dialogs and alike
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(this.primaryStage);
        stage.setTitle("Secondary Window");
        stage.setMinWidth(640);
        stage.setMinHeight(480);

        BorderPane root = new BorderPane();
        root.setCenter(new Label("Secondary Window Content"));

        Scene scene = new Scene(root, 640, 480);

        stage.setScene(scene);
        stage.show();
        */

        DialogStage dialog = new DialogStage(StageStyle.DECORATED,
                Modality.APPLICATION_MODAL,
                primaryStage,
                -1, -1, 320, 240,
                "Title", false);
        // dialog.show();
        dialog.showAndWait();
    }
}

class DialogStage extends Stage {

    private final double width;
    private final double height;

    public DialogStage(StageStyle style, Modality modality, Window owner, double minWidth, double minHeight, double width, double height,
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

        createContent();
    }

    private void createContent() {
        Pane content = createContentPane();
        Scene scene = new Scene(content, width, height);
        setScene(scene);
    }

    private Pane createContentPane() {
        BorderPane root = new BorderPane();
        root.setCenter(new Label("Secondary Window Content"));
        return root;
    }
}
