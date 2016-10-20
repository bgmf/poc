package eu.dzim.guice.fx;

import javax.inject.Inject;

import com.google.inject.Guice;
import com.google.inject.Injector;

import eu.dzim.guice.fx.disposable.Disposable;
import eu.dzim.guice.fx.disposable.DisposableHolder;
import eu.dzim.guice.fx.resource.Resource;
import eu.dzim.guice.fx.schedule.SchedulerExample;
import eu.dzim.guice.fx.service.FXMLLoaderService;
import eu.dzim.guice.fx.service.MyInterface;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainWithGuiceControllerFactory extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}
	
	final com.google.inject.Stage injectionStage = com.google.inject.Stage.DEVELOPMENT;
	final Injector injector = Guice.createInjector(injectionStage, new MyGuiceModule());
	
	@Override
	public void start(Stage stage) throws Exception {
		// instead of
		// FXMLLoader loader = new FXMLLoader(getClass().getResource("GuicyTest.fxml"));
		// you would fetch a FXMLLoader from the respective service, but first you need the service itself
		FXMLLoaderService loaderService = injector.getInstance(FXMLLoaderService.class);
		// get the real loader
		FXMLLoader loader = loaderService.getLoader(getClass().getResource("/fxml/GuicyTest.fxml"));
		// load the content and set it to the scene
		Parent root = loader.load();
		Scene scene = new Scene(root, 1200, 800);
		// ignore these two lines
		// scene.setFill(Color.TRANSPARENT);
		// stage.initStyle(StageStyle.TRANSPARENT);
		// show the stuff
		stage.setScene(scene);
		
		stage.setOnCloseRequest(this::handleCloseRequest);
		
		injector.getInstance(SchedulerExample.class);
		System.err.println(injector.getInstance(Resource.class).getGuranteedString("key"));
		
		stage.show();
	}
	
	private void handleCloseRequest(WindowEvent event) {
		/*
		 * Disposable Extension
		 */
		for (Disposable disposable : injector.getInstance(DisposableHolder.class).getDisposables()) {
			disposable.dispose();
		}
	}
	
	// controller for an FXML without any content, just a showcase...
	public static class ExamplaryGuicyController {
		// necessary, if you have sub-fragments
		@Inject private FXMLLoaderService fxmlLoaderService;
		// some idiotic service interface implementation injected
		@Inject private MyInterface myInterfaceInstance;
		// you can also inject the injector
		@Inject private Injector injector;
		
		@FXML
		protected void initialize() {
			System.out.println("The Guive Injector: " + injector);
			System.out.println("FXMLLoaderService: " + fxmlLoaderService);
			myInterfaceInstance.doSomething();
		}
	}
	
}
