package eu.dzim.tests.fx.controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class DnDController {

    @FXML private HBox top;
    @FXML private BorderPane c1;
    @FXML private BorderPane c2;
    @FXML private BorderPane c3;
    @FXML private BorderPane c4;
    @FXML private BorderPane c5;

    @FXML private Region r1;
    @FXML private Region r2;
    @FXML private Region r3;
    @FXML private Region r4;
    @FXML private Region r5;
    @FXML private Region r6;

    @FXML
    private void initialize() {

        prepareDnDSources(c1, c2, c3, c4, c5);

        prepareDnDTargets(r1, r2, r3, r4, r5, r6);
    }

    private void prepareDnDSources(Node... nodes) {
        if (nodes == null) {
            return;
        }
        for (Node n : nodes) {
            n.setOnDragDetected(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent event) {
                    /* drag was detected, start drag-and-drop gesture */
                    System.out.println("onDragDetected: " + ((Label) ((BorderPane) n).getChildren().get(0)).getText());

                    /* allow any transfer mode */
                    Dragboard db = n.startDragAndDrop(TransferMode.ANY);

                    /* put a string on dragboard */
                    ClipboardContent content = new ClipboardContent();
                    content.putString("" + top.getChildren().indexOf(n));
                    db.setContent(content);
                    SnapshotParameters params = new SnapshotParameters();
                    db.setDragView(n.snapshot(params, null));

                    event.consume();
                }
            });

            n.setOnDragDone(new EventHandler<DragEvent>() {
                public void handle(DragEvent event) {
                    /* the drag-and-drop gesture ended */
                    System.out.println("onDragDone");
                    /* if the data was successfully moved, clear it */
                    if (event.getTransferMode() == TransferMode.MOVE) {
                        // better to move the node here ???
                    }

                    event.consume();
                }
            });
        }
    }

    private void prepareDnDTargets(Node... nodes) {
        if (nodes == null) {
            return;
        }
        for (Node n : nodes) {
            n.setOnDragOver(new EventHandler<DragEvent>() {
                public void handle(DragEvent event) {
                    /* data is dragged over the n */
                    System.out.println("onDragOver");

                    /*
                     * accept it only if it is not dragged from the same node and if it has a string data
                     */
                    if (event.getGestureSource() != n && event.getDragboard().hasString()) {
                        /* allow for both copying and moving, whatever user chooses */
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    }

                    event.consume();
                }
            });

            n.setOnDragEntered(new EventHandler<DragEvent>() {
                public void handle(DragEvent event) {
                    /* the drag-and-drop gesture entered the n */
                    System.out.println("onDragEntered");
                    /* show to the user that it is an actual gesture n */
                    if (event.getGestureSource() != n && event.getDragboard().hasString()) {
                        // do something to indicate, that we entered a draggable area
                        n.setStyle("-fx-background-color: green;");
                    }

                    event.consume();
                }
            });

            n.setOnDragExited(new EventHandler<DragEvent>() {
                public void handle(DragEvent event) {
                    // do something to indicate, that we left a draggable area
                    n.setStyle("-fx-background-color: red;");
                    event.consume();
                }
            });

            n.setOnDragDropped(new EventHandler<DragEvent>() {
                public void handle(DragEvent event) {
                    /* data dropped */
                    System.out.println("onDragDropped");
                    /* if there is a string data on dragboard, read it and use it */
                    boolean success = false;
                    Dragboard db = event.getDragboard();
                    if (db.hasString()) {
                        // n.setText(db.getString());
                        int nIndex = top.getChildren().indexOf(n);
                        int index = Integer.parseInt(db.getString());
                        if (index == nIndex + 1 || index == nIndex - 1) {
                            // do nothing
                        } else {
                            Node spacerToMove = top.getChildren().get(index - 1);
                            Node paneToMove = top.getChildren().get(index);
                            top.getChildren().remove(spacerToMove);
                            top.getChildren().remove(paneToMove);
                            top.getChildren().add(nIndex + 1, spacerToMove);
                            top.getChildren().add(nIndex + 1, paneToMove);
                            success = true;
                        }
                    }

                    /*
                     * let the source know whether the string was successfully transferred and used
                     */
                    event.setDropCompleted(success);

                    event.consume();
                }
            });
        }
    }
}
