package eu.dzim.tests.fx.controls;

import java.util.function.BiConsumer;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class ResizableCanvas extends Canvas {
	
	public ResizableCanvas() {
		// Redraw canvas when size changes.
		// bindDrawFunction(this::draw);
	}
	
	private Void draw(double width, double height) {
		
		GraphicsContext gc = getGraphicsContext2D();
		gc.clearRect(0, 0, width, height);
		
		gc.setStroke(Color.RED);
		gc.strokeLine(0, 0, width, height);
		gc.strokeLine(0, height, width, 0);
		
		return null;
	}
	
	@Override
	public boolean isResizable() {
		return true;
	}
	
	@Override
	public double prefWidth(double height) {
		return getWidth();
	}
	
	@Override
	public double prefHeight(double width) {
		return getHeight();
	}
	
	public void bindTo(Pane pane) {
		// Bind canvas size to pane size.
		this.widthProperty().bind(pane.widthProperty());
		this.heightProperty().bind(pane.heightProperty());
	}
	
	public void bindDrawFunction(BiConsumer<Double, Double> function) {
		widthProperty().addListener(evt -> function.accept(this.getWidth(), this.getHeight()));
		heightProperty().addListener(evt -> function.accept(this.getWidth(), this.getHeight()));
	}
}