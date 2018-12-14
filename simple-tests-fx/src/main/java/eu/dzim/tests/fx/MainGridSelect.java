package eu.dzim.tests.fx;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainGridSelect extends Application {

    private final Class<?>[] availableNodes = new Class<?>[] { Label.class, Button.class, CheckBox.class, HBox.class };
    private final Random random = new Random(System.currentTimeMillis());
    private final Pattern pattern = Pattern.compile("item_(\\d+)_(\\d+)");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        stage.setTitle("Grid Select");

        GridPane root = buildContent();
        Scene scene = new Scene(root, 1200, 800);
        initialize(stage, scene, root);

        stage.setScene(scene);
        stage.show();
    }

    private GridPane buildContent() {
        GridPane pane = new GridPane();

        pane.setHgap(5.0);
        pane.setVgap(5.0);

        final int max = 4;
        for (int i = 0; i < max; i++) {
            for (int j = 0; j < max; j++) {
                Node n = getRandomNode(pane, max, i, j);
                GridPane.setConstraints(n, i, j);
                pane.getChildren().add(n);
            }
        }

        return pane;
    }

    private Node getRandomNode(GridPane pane, int max, int i, int j) {

        int r = random.nextInt(max);
        Class<?> clazz = availableNodes[r];

        Node n;
        if (Label.class.isAssignableFrom(clazz)) {
            n = new Label(String.format(Locale.ROOT, "%d, %d", i, j));
        } else if (Button.class.isAssignableFrom(clazz)) {
            n = new Button(String.format(Locale.ROOT, "%d, %d", i, j));
        } else if (CheckBox.class.isAssignableFrom(clazz)) {
            n = new CheckBox(String.format(Locale.ROOT, "%d, %d", i, j));
        } else if (HBox.class.isAssignableFrom(clazz)) {
            n = new HBox();
            ((HBox) n).setSpacing(5.0);
            Label l = new Label("" + i);
            l.setOnMouseClicked(e -> pane.fireEvent(e.copyFor(n, pane)));
            Button b = new Button("" + j);
            b.setOnMouseClicked(e -> pane.fireEvent(e.copyFor(n, pane)));
            ((HBox) n).getChildren().addAll(l, b);
        } else {
            return null;
        }
        n.setId("item_" + i + "_" + j);
        n.setOnMouseClicked(e -> pane.fireEvent(e.copyFor(n, pane)));
        return n;
    }

    private void initialize(Stage stage, Scene scene, GridPane pane) {
        pane.setOnMouseClicked(e -> {
            pane.getChildren().stream()
                    .filter(n -> n.getLayoutX() <= e.getX() && e.getX() <= (n.getLayoutX() + n.getLayoutBounds().getWidth()) && n.getLayoutY() <= e
                            .getY() && e.getY() <= (n.getLayoutY() + n.getLayoutBounds().getHeight())).forEach(n -> handleClick(e, n));
        });
    }

    private void handleClick(MouseEvent e, Node targetNode) {
        System.err.println(targetNode);
        String id = targetNode.getId();
        if (id != null) {
            Matcher m = pattern.matcher(id);
            if (m.matches()) {
                String colString = m.group(1);
                String rowString = m.group(2);
                System.err.printf("Grid element: Column=%s, Row=%s", colString, rowString);
            }
        }
    }
}
