package eu.dzim.tests.fx;

import org.jpedal.PdfDecoderFX;
import org.jpedal.examples.viewer.OpenViewerFX;
import org.jpedal.exception.PdfException;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class PDFTestController {
	
	@FXML private BorderPane root;
	
	@FXML
	protected void initialize() {
		
		VBox center = new VBox();
		root.setCenter(center);
		
		OpenViewerFX fx = new OpenViewerFX(center, null);
		fx.setupViewer();
	}
}
