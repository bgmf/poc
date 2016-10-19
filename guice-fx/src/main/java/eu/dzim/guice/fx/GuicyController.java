package eu.dzim.guice.fx;

import javax.inject.Inject;

import com.google.inject.Injector;

import eu.dzim.guice.fx.disposable.Disposable;
import eu.dzim.guice.fx.service.FXMLLoaderService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GuicyController implements Disposable {
	
	@Inject
	private FXMLLoaderService fxmlLoaderService;
	
	@Inject
	private Injector injector;
	
	@FXML
	private BorderPane sub;
	@FXML
	private GuicySubController subController;
	
	@FXML
	protected void initialize() {
		System.out.println(this.getClass().getSimpleName() + ": The Guive Injector: " + injector);
		System.out.println(this.getClass().getSimpleName() + ": FXMLLoaderService: " + fxmlLoaderService);
	}
	
	@FXML
	public void handleMenuFileExit(ActionEvent event) {
		// Platform.exit();
		((Stage) sub.getScene().getWindow()).fireEvent(new WindowEvent(((Stage) sub.getScene().getWindow()), WindowEvent.WINDOW_CLOSE_REQUEST));
	}
	
	@Override
	public void dispose() {
		System.out.println(this.getClass().getSimpleName() + ": dispose");
	}
}
