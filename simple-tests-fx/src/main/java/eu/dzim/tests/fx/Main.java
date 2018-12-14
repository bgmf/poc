package eu.dzim.tests.fx;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class Main extends Application {

    // Platzhalter
    Label labelb = new Label("Ich putz hier nur.");
    // ____________________________________________________________________________
    // Set Panels - Bord=Window, vBoxPosition=bord.left, vBoxColor=bord.right, hBoxNode=bord.top,
    // hBoxBottom=bord.bottm, paneCenter=bord.paneCenterter
    // ____________________________________________________________________________
    private BorderPane root = new BorderPane();
    private VBox vBoxPosition = new VBox();
    private VBox vBoxColor = new VBox();
    private HBox hBoxNode = new HBox();
    private HBox hBoxBottom = new HBox();
    private Pane paneCenter = new Pane();
    // ____________________________________________________________________________
    // Active Objekt(Circle or Rectangle...)
    // ____________________________________________________________________________
    private Node activenode;
    // ____________________________________________________________________________
    // Create Color.
    // ____________________________________________________________________________
    private Color color;
    private double rot;
    private double grün;
    private double blau;
    // ____________________________________________________________________________
    // Create panels for inside Bord.left
    // -x = Label+SliderXposi, -y = Label+SliderZposi, -z = Label+SliderZposi,
    // ____________________________________________________________________________
    private VBox paneXPos = new VBox();
    private VBox paneYPos = new VBox();
    private VBox paneZPos = new VBox();
    // ____________________________________________________________________________
    private HBox paneButtonPos = new HBox();
    // ____________________________________________________________________________
    // Create panels for inside Bord.right
    // -r = Label+SliderColR, -g = Label+SliderColG, -b = Label+SliderColB,
    // ____________________________________________________________________________
    private VBox paneColRed = new VBox();
    private VBox paneColGreen = new VBox();
    private VBox paneColBlue = new VBox();
    // ____________________________________________________________________________
    private HBox paneButtonCol = new HBox();
    // ____________________________________________________________________________
    // Create panels for inside Bord.top
    // panelNode = Slider - Nodes
    // ____________________________________________________________________________
    private Pane panelNode = new VBox();
    private Slider sliderNode;
    private Pane platzhalter = new Pane();
    // ____________________________________________________________________________
    // Create Labels for Sliders - Position and Color.
    // ____________________________________________________________________________
    private Label labelXPos;
    private Label labelYPos;
    private Label labelZPos;
    // ____________________________________________________________________________
    private Label labelColRed;
    private Label labelColGreen;
    private Label labelColBlue;
    // ____________________________________________________________________________
    // Create Slider for Position and Color.
    // ____________________________________________________________________________
    private Slider sliderXPos;
    private Slider sliderYPos;
    private Slider sliderZPos;
    // ____________________________________________________________________________
    private Slider sliderRed = new Slider(0, 1, 0);
    private Slider sliderGreen = new Slider(0, 1, 0);
    private Slider sliderBlue = new Slider(0, 1, 0);
    // ____________________________________________________________________________
    private Button buttonSliderCur = new Button("ResetCur");
    private Button buttonSliderAll = new Button("ResetAll");
    private Button buttonColorCur = new Button("ResetCur");
    private Button buttonColorAll = new Button("ResetAll");

    // ____________________________________________________________________________
    // Create the Objects/Nodes/Shapes...
    // ____________________________________________________________________________
    private double radius = 20.0;
    private Circle circle = new Circle();
    private Rectangle rect = new Rectangle();
    private Text texti = new Text();

    private Node[] nodelist = { circle, rect, texti };

    // ____________________________________________________________________________
    // Create Array for old Values.
    // ____________________________________________________________________________
    private double[][] position = new double[3][3]; // 0 0 = circle X, 1 0 = rect X, 2 0 = text X
    private double[][] colorVals = new double[3][3]; // 0 0 = circle R, 1 0 = rect R, 2 0 = text R

    // ____________________________________________________________________________
    //
    // ____________________________________________________________________________
    public static void main(String[] args) {
        launch(args);
    }

    // ____________________________________________________________________________
    // Initialize most of the created Variables.
    // ____________________________________________________________________________
    private void setStyle() {
        // ____________________________________________________________________________
        // ____________________________________________________________________________
        double paneCenterWidth = paneCenter.getLayoutBounds().getWidth();
        double paneCenterHeight = paneCenter.getLayoutBounds().getHeight();
        sliderXPos = new Slider(0, paneCenterWidth - 2 * radius, 0);
        sliderYPos = new Slider(0, paneCenterHeight - 3 * radius, 0);
        sliderZPos = new Slider(0, 100, 0);
        System.out.println("\n\nLayoutBonds:" + paneCenter.getLayoutBounds());
        // ____________________________________________________________________________
        sliderXPos.setPadding(new Insets(5, 5, 5, 5)); // Distance top, right, bottom, left
        sliderXPos.setShowTickMarks(true);
        sliderXPos.setShowTickLabels(true);
        sliderXPos.setMajorTickUnit(260);
        sliderXPos.setMinorTickCount(2);
        // ____________________________________________________________________________
        sliderYPos.setPadding(new Insets(5, 5, 5, 5));
        sliderYPos.setShowTickMarks(true);
        sliderYPos.setShowTickLabels(true);
        sliderYPos.setMajorTickUnit(200);
        sliderYPos.setMinorTickCount(2);
        // ____________________________________________________________________________
        sliderZPos.setDisable(true);
        sliderZPos.setPadding(new Insets(5, 5, 5, 5));
        sliderZPos.setShowTickMarks(true);
        sliderZPos.setShowTickLabels(true);
        sliderZPos.setMajorTickUnit(20);
        sliderZPos.setMinorTickCount(2);
        // ____________________________________________________________________________
        sliderRed.setShowTickMarks(true);
        sliderRed.setShowTickLabels(true);
        sliderRed.setMajorTickUnit(0.25);
        sliderRed.setMinorTickCount(1);
        // ____________________________________________________________________________
        sliderGreen.setShowTickMarks(true);
        sliderGreen.setShowTickLabels(true);
        sliderGreen.setMajorTickUnit(0.25);
        sliderGreen.setMinorTickCount(1);
        // ____________________________________________________________________________
        sliderBlue.setShowTickMarks(true);
        sliderBlue.setShowTickLabels(true);
        sliderBlue.setMajorTickUnit(0.25);
        sliderBlue.setMinorTickCount(1);
        // ____________________________
        sliderNode = new Slider(0, 3, 0);
        // sliderNode.setPrefWidth(hBoxNode.getLayoutBounds().getWidth() * 0.75);
        // sliderNode.prefWidthProperty().bind(panelNode.widthProperty().multiply(.75));
        sliderNode.maxWidth(Double.MAX_VALUE);
        sliderNode.setPadding(new Insets(5, 5, 5, 5));
        sliderNode.setShowTickMarks(true);
        sliderNode.setShowTickLabels(true);
        sliderNode.setSnapToTicks(true);
        sliderNode.setMajorTickUnit(1);
        sliderNode.setMinorTickCount(0);

        // ____________________________________________________________________________
        // Transform the Mark from Number to Label .
        // ____________________________________________________________________________
        sliderNode.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double n) {
                if (n == 0)
                    return "None";
                if (n == 1)
                    return "Circle";
                if (n == 2)
                    return "Rectangle";
                else if (n == 3)
                    return "Text";
                else
                    return null;
            }

            @Override
            public Double fromString(String s) {
                switch (s) {
                case "None":
                    return 0d;
                case "Circle":
                    return 1d;
                case "Rectangle":
                    return 2d;
                case "Text":
                    return 3d;
                default:
                    return null;
                }
            }
        });

        // ____________________________________________________________________________
        // Initialize the Labels for Position and Color.
        // ____________________________________________________________________________
        labelXPos = new Label("X-Posi:");
        labelXPos.setPadding(new Insets(5, 5, 5, 5));
        labelYPos = new Label("Y-Posi:");
        labelYPos.setPadding(new Insets(5, 5, 5, 5));
        labelZPos = new Label("Z-Posi:");
        labelZPos.setPadding(new Insets(5, 5, 5, 5));
        // ____________________________________________________________________________
        labelColRed = new Label("Rot:");
        labelColRed.setPadding(new Insets(5, 5, 5, 5));
        labelColGreen = new Label("Grün:");
        labelColGreen.setPadding(new Insets(5, 5, 5, 5));
        labelColBlue = new Label("Blau:");
        labelColBlue.setPadding(new Insets(5, 5, 5, 5));

        // ____________________________________________________________________________
        // Adding the Labels and Slider to the Panels.
        // -x,y,z at Bord.left; -r,g,b at Bord.right; Node at Bord.top
        // ____________________________________________________________________________
        paneXPos.getChildren().addAll(labelXPos, sliderXPos);
        paneXPos.setStyle("-fx-border-style: solid; -fx-border-color: black; -fx-border-width: 0 0 1 0;");
        paneYPos.getChildren().addAll(labelYPos, sliderYPos);
        paneYPos.setStyle("-fx-border-style: solid; -fx-border-color: black; -fx-border-width: 0 0 1 0;");
        paneZPos.getChildren().addAll(labelZPos, sliderZPos);
        paneZPos.setStyle("-fx-border-style: solid; -fx-border-color: black; -fx-border-width: 0 0 1 0;");
        // ____________________________________________________________________________
        paneButtonPos.getChildren().addAll(buttonSliderCur, buttonSliderAll);
        paneButtonPos.setStyle("-fx-padding: 5 0 5 25;");
        // ____________________________________________________________________________
        paneColRed.getChildren().addAll(labelColRed, sliderRed);
        paneColRed.setStyle("-fx-border-style: solid; -fx-border-color: black; -fx-border-width: 0 0 1 0;");
        paneColGreen.getChildren().addAll(labelColGreen, sliderGreen);
        paneColGreen.setStyle("-fx-border-style: solid; -fx-border-color: black; -fx-border-width: 0 0 1 0;");
        paneColBlue.getChildren().addAll(labelColBlue, sliderBlue);
        paneColBlue.setStyle("-fx-border-style: solid; -fx-border-color: black; -fx-border-width: 0 0 1 0;");
        paneButtonCol.getChildren().addAll(buttonColorCur, buttonColorAll);
        paneButtonCol.setStyle("-fx-border-style: solid; -fx-border-color: black; -fx-border-width: 0 0 1 0;" + "-fx-padding: 5 0 5 25;");
        // ____________________________________________________________________________
        panelNode.getChildren().addAll(sliderNode);
        // ____________________________________________________________________________
        platzhalter.getChildren().add(labelb);
        labelb.setStyle("-fx-font: 30px Serif");

        // Creating the Shapes
        // ____________________________________________________________________________
        color = new Color(0, 0, 0, 1.0);
        circle.setRadius(20.0);
        circle.setCenterX(20.0);
        circle.setCenterY(20.0);
        circle.setFill(color);
        // ____________________________________________________________________________
        rect.setWidth(2 * radius);
        rect.setHeight(2 * radius);
        rect.setFill(color);
        rect.setStroke(Color.BLACK);
        // ____________________________________________________________________________
        texti.setText("Christbaumwerfer");
        texti.setFont(new Font(20));
    }

    // ____________________________________________________________________________
    // Creating the Methods for Translation and Color Change.
    // ____________________________________________________________________________
    private void transX(Node node, double valx) {
        labelXPos.setText("X-Posi: " + round(sliderXPos.getValue()));
        try {
            node.setTranslateX(valx);
        } catch (Exception e) {
        }
    }

    // ____________________________________________________________________________
    private void transY(Node node, double valy) {
        labelYPos.setText("Y-Posi: " + round(sliderYPos.getValue()));
        try {
            node.setTranslateY(valy);
        } catch (Exception e) {
        }
    }

    // ____________________________________________________________________________
    private void transZ(Node node, double valz) {
        labelZPos.setText("Z-Posi: " + round(sliderZPos.getValue()));
        try {
            node.setTranslateZ(valz);
        } catch (Exception e) {
        }
    }

    // ____________________________________________________________________________
    public void repaintt(Node node) {
        color = new Color(rot, grün, blau, 1.0);
        try {
            ((Shape) node).setFill(color);
        } catch (Exception e) {
        }
    }

    // ____________________________________________________________________________
    // Setter for old Values of the Sliders - Position and Color.
    // ____________________________________________________________________________
    public void setOldSliderPos(Node node, double oldX, double oldY, double oldZ) {
        if (node == circle) {
            position[0][0] = oldX;
            position[0][1] = oldY;
            position[0][2] = oldZ;
        }
        if (node == rect) {
            position[1][0] = oldX;
            position[1][1] = oldY;
            position[1][2] = oldZ;
        }
        if (node == texti) {
            position[2][0] = oldX;
            position[2][1] = oldY;
            position[2][2] = oldZ;
        }
    }

    // ____________________________________________________________________________
    public void setOldCol(Node node, double oldR, double oldG, double oldB) {
        if (node == circle) {
            colorVals[0][0] = oldR;
            colorVals[0][1] = oldG;
            colorVals[0][2] = oldB;
        }
        if (node == rect) {
            colorVals[1][0] = oldR;
            colorVals[1][1] = oldG;
            colorVals[1][2] = oldB;
        }
        if (node == texti) {
            colorVals[2][0] = oldR;
            colorVals[2][1] = oldG;
            colorVals[2][2] = oldB;
        }
    }

    // ____________________________________________________________________________
    // Getter for old Values of the Objects/Nodes/Shapes.
    // Get the old Value and change the current Value of the Slider to the old.
    // ____________________________________________________________________________
    public void getOldSliderPos() {
        if (activenode == circle) {
            sliderXPos.setValue(position[0][0]);
            sliderYPos.setValue(position[0][1]);
            sliderZPos.setValue(position[0][2]);
        } else if (activenode == rect) {
            sliderXPos.setValue(position[1][0]);
            sliderYPos.setValue(position[1][1]);
            sliderZPos.setValue(position[1][2]);
        } else if (activenode == texti) {
            sliderXPos.setValue(position[2][0]);
            sliderYPos.setValue(position[2][1]);
            sliderZPos.setValue(position[2][2]);
        }
    }

    // ____________________________________________________________________________
    public void getOldCol() {
        if (activenode == circle) {
            sliderRed.setValue(colorVals[0][0]);
            sliderGreen.setValue(colorVals[0][1]);
            sliderBlue.setValue(colorVals[0][2]);
        } else if (activenode == rect) {
            sliderRed.setValue(colorVals[1][0]);
            sliderGreen.setValue(colorVals[1][1]);
            sliderBlue.setValue(colorVals[1][2]);
        } else if (activenode == texti) {
            sliderRed.setValue(colorVals[2][0]);
            sliderGreen.setValue(colorVals[2][1]);
            sliderBlue.setValue(colorVals[2][2]);
        }
    }

    // ____________________________________________________________________________
    // Resetmethods Position and Color
    // ____________________________________________________________________________
    public void resetPos(Node node, double mod) {
        if (mod == 1) {
            for (int i = 0; i < nodelist.length; i++) {
                node = nodelist[i];
                setOldSliderPos(node, 0, 0, 0);
                getOldSliderPos();
                transX(node, 0);
                transY(node, 0);
                transZ(node, 0);
            }
        } else {
            setOldSliderPos(node, 0, 0, 0);
            getOldSliderPos();
            transX(node, 0);
            transY(node, 0);
            transZ(node, 0);
        }
    }

    public void resetCol(Node node, double mod) {
        if (mod == 1) {
            for (int i = 0; i < nodelist.length; i++) {
                node = nodelist[i];
                setOldCol(node, 0, 0, 0);
                getOldCol();
                ((Shape) node).setFill(Color.BLACK);
            }
        } else {
            setOldCol(node, 0, 0, 0);
            getOldCol();
            ((Shape) node).setFill(Color.BLACK);
        }
    }

    // ____________________________________________________________________________
    // round 1,1234 -> 1,12 and 1,4 -> 1
    // ____________________________________________________________________________
    public double round(final double value) {
        return Math.round(100.0 * value) / 100.0;
    }

    // ____________________________________________________________________________
    public double roundi(final double value) {
        return Math.round(10.0 * value) / 10.0;
    }

    // ____________________________________________________________________________
    // Check Bounds to identify collisions with other Shapes
    // ____________________________________________________________________________
    public void checkBounds(Node node) {
        boolean collision = false;
        for (Node static_sblock : nodelist) {
            if (static_sblock != node) {
                if (node.getBoundsInParent().intersects(static_sblock.getBoundsInParent())) {
                    collision = true;
                }
            }
        }
        if (collision) {
            ((Shape) node).setFill(Color.GREEN);
        } else {
            ((Shape) node).setFill(color);
        }
    }

    // ____________________________________________________________________________
    // Now get it started. start.method creates the Stage/Window.
    // ____________________________________________________________________________
    @Override
    public void start(Stage primaryStage) {
        // ____________________________________________________________________________
        // First create a Rectangle to get the size of the screen.
        // ____________________________________________________________________________
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, screen.getWidth() / 2, screen.getHeight() / 2);

        // ____________________________________________________________________________
        // ____________________________________________________________________________
        // Adding big panels to greater Panels.
        // Greater Panels will be added to Bord for better Placement.
        // ____________________________________________________________________________
        vBoxPosition.getChildren().addAll(paneXPos, paneYPos, paneZPos, paneButtonPos);
        vBoxPosition.setPrefWidth(200);
        vBoxColor.getChildren().addAll(paneColRed, paneColGreen, paneColBlue, paneButtonCol);
        vBoxColor.setPrefWidth(200);
        HBox.setHgrow(panelNode, Priority.ALWAYS);
        HBox.setMargin(panelNode, new Insets(0.0, 75.0, 0.0, 75.0));
        hBoxNode.getChildren().addAll(panelNode);
        hBoxNode.setAlignment(Pos.CENTER);
        hBoxNode.setPrefHeight(30);
        hBoxBottom.getChildren().add(platzhalter);
        hBoxBottom.setAlignment(Pos.CENTER);
        hBoxBottom.setPrefHeight(30);
        paneCenter.getChildren().addAll(circle, rect, texti);
        root.setCenter(paneCenter);
        root.setLeft(vBoxPosition);
        root.setRight(vBoxColor);
        root.setTop(hBoxNode);
        root.setBottom(hBoxBottom);
        root.layout(); // With this I get the Width and Height even before the Stage is build.

        // ____________________________________________________________________________
        // Call setStyle() to initialize all the Labels and Sliders etc.
        // ____________________________________________________________________________
        setStyle();
        // ____________________________________________________________________________

        // ____________________________________________________________________________
        // Style the greater Panels, Border and Color(Color change thumbs of the
        // Slider - why?).
        // ____________________________________________________________________________
        vBoxPosition.setStyle("-fx-border-style: solid; -fx-border-width: 0 1 0 1; -fx-color: Red;");
        vBoxColor.setStyle("-fx-border-style: solid; -fx-border-width: 0 1 0 1; -fx-color: Blue;");
        hBoxNode.setStyle("-fx-border-style: solid; -fx-border-width: 1 1 1 1");
        hBoxBottom.setStyle("-fx-border-style: solid; -fx-border-width: 1 1 1 1");

        // ____________________________________________________________________________
        // Adding the greater Panels to Board and place them at the right Position.
        // ____________________________________________________________________________

        // ____________________________________________________________________________
        // ChangeListener for the Slider.
        // MainListener is the Node_Slider. When Node is chosen we can add the Listeners
        // to the other Sliders - Position and Color. But first ask whether the Node
        // has CHANGED. Not a normal Listener for the Node_Slider!
        // ____________________________________________________________________________

        sliderNode.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            // ____________________________________________________________________________
            // Set the activenode and get the old Values for Position and Color.
            // ____________________________________________________________________________
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (Math.round(sliderNode.getValue()) == 1) {
                    activenode = circle;
                    getOldSliderPos();
                    getOldCol();
                } else if (Math.round(sliderNode.getValue()) == 2) {
                    activenode = rect;
                    getOldSliderPos();
                    getOldCol();
                } else if (Math.round(sliderNode.getValue()) == 3) {
                    activenode = texti;
                    getOldSliderPos();
                    getOldCol();
                } else
                    ;

                // ____________________________________________________________________________
                // Now add a Listener to the Slider. Normal one, because we want to repaint our
                // Object while changing the Slider. Send new Coords and activenode for Change.
                // ____________________________________________________________________________
                sliderXPos.valueProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldv, Number newv) {
                        transX(activenode, (double) newv);
                        checkBounds(activenode);
                    }
                });

                // ____________________________________________________________________________
                // Now the HAS_CHANGED_LISTENER because we want to save the last Value of the
                // Slider. So we can change the Object/Node and still have the Values of the
                // previous Object.
                // ____________________________________________________________________________
                sliderXPos.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        setOldSliderPos(activenode, sliderXPos.getValue(), sliderYPos.getValue(), sliderZPos.getValue());
                    }
                });

                // ____________________________________________________________________________
                // Just repeat for every Slider - Position and Color.
                // ____________________________________________________________________________
                sliderYPos.valueProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldv, Number newv) {
                        transY(activenode, (double) newv);
                        checkBounds(activenode);
                    }
                });
                sliderYPos.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        setOldSliderPos(activenode, sliderXPos.getValue(), sliderYPos.getValue(), sliderZPos.getValue());
                    }
                });
                // ____________________________________________________________________________
                sliderZPos.valueProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldv, Number newv) {
                        transZ(activenode, (double) newv);
                    }
                });
                sliderZPos.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        setOldSliderPos(activenode, sliderXPos.getValue(), sliderYPos.getValue(), sliderZPos.getValue());
                    }
                });
                // ____________________________________________________________________________
                // Color
                // ____________________________________________________________________________
                sliderRed.valueProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldv, Number newv) {
                        rot = (double) newv;
                        repaintt(activenode);
                    }
                });
                sliderRed.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        setOldCol(activenode, sliderRed.getValue(), sliderGreen.getValue(), sliderBlue.getValue());
                    }
                });
                // ____________________________________________________________________________
                sliderGreen.valueProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldv, Number newv) {
                        grün = (double) newv;
                        repaintt(activenode);
                    }
                });
                sliderGreen.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        setOldCol(activenode, sliderRed.getValue(), sliderGreen.getValue(), sliderBlue.getValue());
                    }
                });
                // ____________________________________________________________________________
                sliderBlue.valueProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldv, Number newv) {
                        blau = (double) newv;
                        repaintt(activenode);
                    }
                });
                sliderBlue.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        setOldCol(activenode, sliderRed.getValue(), sliderGreen.getValue(), sliderBlue.getValue());
                    }
                });
                // ____________________________________________________________________________
                buttonSliderCur.setOnAction(event -> {
                    resetPos(activenode, 0);
                });
                buttonColorCur.setOnAction(event -> {
                    resetCol(activenode, 0);
                });
                buttonSliderAll.setOnAction(event -> {
                    resetPos(activenode, 1);
                });
                buttonColorAll.setOnAction(event -> {
                    resetCol(activenode, 1);
                });
                // ____________________________________________________________________________
                //
                // ____________________________________________________________________________
                if (activenode != null) {
                    activenode.setOnMousePressed(eventP -> {
                        activenode.setOnMouseDragged(eventD -> {
                            sliderXPos.setValue(sliderXPos.getValue() + eventD.getX());
                            sliderYPos.setValue(sliderYPos.getValue() + eventD.getY());
                        });
                    });
                    activenode.setOnMouseReleased(eventO -> {
                        setOldSliderPos(activenode, sliderXPos.getValue(), sliderYPos.getValue(), sliderZPos.getValue());
                    });
                }

            }
        });
        // ____________________________________________________________________________
        // Try to create the Window.
        // ____________________________________________________________________________

        try {
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
