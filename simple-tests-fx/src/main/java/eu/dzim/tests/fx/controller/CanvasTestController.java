package eu.dzim.tests.fx.controller;

import eu.dzim.tests.fx.controls.ResizableCanvas;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class CanvasTestController {

    @FXML private TextField text1;
    @FXML private TextField text2;
    @FXML private TextField text3;
    @FXML private Button button1;
    @FXML private Button button2;
    @FXML private StackPane canvasPane;
    @FXML private ResizableCanvas canvas;

    private GraphicsContext gc;

    @FXML
    public void initialize() {
        gc = canvas.getGraphicsContext2D();
        canvas.bindTo(canvasPane);
        canvas.bindDrawFunction(this::printShape);
    }

    @FXML
    public void handleButton1(ActionEvent event) {
        System.err.println(canvas.getHeight() + ":" + canvas.getWidth());
    }

    @FXML
    public void handleButton2(ActionEvent event) {
        System.err.println(canvas.getHeight() + ":" + canvas.getWidth());
    }

    public void printShape(double width, double height) {

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, 600, 480);

        Point2D p1 = new Point2D(0.0, 0.0);
        Point2D p2 = new Point2D(width, height);
        Point2D p3 = new Point2D(width, 0.0);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(5);
        gc.strokeLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        gc.strokeLine(p2.getX(), p2.getY(), p3.getX(), p3.getY());
        gc.strokeLine(p3.getX(), p3.getY(), p1.getX(), p1.getY());
    }
}
