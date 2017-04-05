package eu.dzim.shared.fx.ui.controller;

import eu.dzim.shared.disposable.Disposable;
import eu.dzim.shared.util.SingleAcceptor;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class ImageContainerController implements Disposable, SingleAcceptor<ImageContainerController.ImageData> {
	
	@FXML private HBox box;
	@FXML private ScrollPane scroll;
	@FXML private ImageView iv;
	
	private ImageContainerController.ImageData data = null;
	private ChangeListener<Number> zoomListener = this::handleZoomChange;
	
	public ImageContainerController() {
		// sonar
	}
	
	@FXML
	protected void initialize() {
		// nothing to do
	}
	
	public void accept(ImageData data) {
		
		if (this.data != null)
			this.data.zoom.removeListener(zoomListener);
		
		this.data = data;
		
		iv.setImage(data.getImage());
		
		data.zoom.addListener(zoomListener);
	}
	
	private void handleZoomChange(ObservableValue<? extends Number> obs, Number o, Number n) {
		iv.setFitHeight(data.getImage().getHeight() * n.doubleValue());
		iv.setFitWidth(data.getImage().getWidth() * n.doubleValue());
	}
	
	@Override
	public void dispose() {
		iv.setImage(null);
	}
	
	public static class ImageData {
		
		private final Image image;
		private final Rectangle2D rect;
		
		private final DoubleProperty zoom = new SimpleDoubleProperty();
		
		public ImageData(final Image image, final Rectangle2D rect, final DoubleProperty origZoom) {
			this.image = image;
			this.rect = rect;
			this.zoom.bind(origZoom);
		}
		
		public Image getImage() {
			return image;
		}
		
		public Rectangle2D getRect() {
			return rect;
		}
		
		public DoubleProperty getZoom() {
			return zoom;
		}
	}
}
