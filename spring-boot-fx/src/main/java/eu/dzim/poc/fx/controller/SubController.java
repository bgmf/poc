package eu.dzim.poc.fx.controller;

import javax.annotation.PreDestroy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import eu.dzim.poc.fx.service.FXMLLoaderService;
import eu.dzim.poc.fx.service.HelloWorldService;
import eu.dzim.shared.disposable.Disposable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

@Component
@Scope("prototype")
public class SubController implements Disposable {
	
	private static final Logger LOG = LogManager.getLogger(SubController.class);
	
	@Autowired private FXMLLoaderService fxmlLoaderService;
	
	@Autowired private HelloWorldService someService;
	
	@Autowired private ConfigurableApplicationContext context;
	
	@FXML private Button button;
	
	public SubController() {}
	
	@FXML
	protected void initialize() {
		LOG.info("The Context: " + context);
		LOG.info("FXMLLoaderService: " + fxmlLoaderService);
	}
	
	@FXML
	public void handleButton(ActionEvent event) {
		LOG.info(someService.getHelloMessage());
	}
	
	@Override
	public void dispose() {
		LOG.info("disposing");
		System.out.println(this.getClass().getSimpleName() + ": dispose");
	}
	
	@PreDestroy
	public void preDestroy() {
		dispose();
	}
}
