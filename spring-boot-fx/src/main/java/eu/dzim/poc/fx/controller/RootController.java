package eu.dzim.poc.fx.controller;

import javax.annotation.PreDestroy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import eu.dzim.poc.fx.service.FXMLLoaderService;
import eu.dzim.shared.disposable.Disposable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

@Component
@Scope("prototype")
public class RootController implements Disposable {
	
	private static final Logger LOG = LogManager.getLogger(RootController.class);
	
	@Autowired private FXMLLoaderService fxmlLoaderService;
	
	@Autowired private ConfigurableApplicationContext context;
	
	@FXML private BorderPane sub;
	@FXML private SubController subController;
	
	public RootController() {}
	
	@FXML
	protected void initialize() {
		LOG.info("The Context: " + context);
		LOG.info("FXMLLoaderService: " + fxmlLoaderService);
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
	
	@PreDestroy
	public void preDestroy() {
		dispose();
	}
}
