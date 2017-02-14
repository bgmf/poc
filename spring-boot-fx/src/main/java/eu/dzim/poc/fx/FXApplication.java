package eu.dzim.poc.fx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import eu.dzim.poc.fx.model.ApplicationModel;
import eu.dzim.poc.fx.resource.Resource;
import eu.dzim.poc.fx.service.FXMLLoaderService;
import eu.dzim.poc.fx.util.Utils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class FXApplication extends Application implements CommandLineRunner {
	
	private static final Logger LOG = LogManager.getLogger(FXApplication.class);
	
	@Override
	public void run(String... args) {
		// something to call prior to the real application starts?
	}
	
	private static String[] savedArgs;
	
	// locally stored Spring Boot application context
	private ConfigurableApplicationContext applicationContext;
	
	// we need to override the FX init process for Spring Boot
	@Override
	public void init() throws Exception {
		
		// set Thread name
		Thread.currentThread().setName("main");
		
		// LOG.debug("Init JavaFX application");
		applicationContext = SpringApplication.run(getClass(), savedArgs);
		applicationContext.getAutowireCapableBeanFactory().autowireBean(this);
	}
	
	// ... and close our context on stop of the FX part
	@Override
	public void stop() throws Exception {
		// LOG.debug("Stop JavaFX application");
		super.stop();
		applicationContext.close();
	}
	
	protected static void launchApp(Class<? extends FXApplication> appClass, String[] args) {
		FXApplication.savedArgs = args;
		Application.launch(appClass, args);
	}
	
	@Autowired private FXMLLoaderService fxmlLoaderService;
	@Autowired private Resource mResource;
	@Autowired private ApplicationModel mApplicationModel;
	
	@Override
	public void start(Stage primaryStage) {
		
		// set Thread name
		Thread.currentThread().setName("main-ui");
		
		try {
			FXMLLoader loader = fxmlLoaderService.getLoader(Utils.getFXMLResource("Root"), mResource.getResourceBundle());
			
			Pane root = loader.load();
			
			Scene scene = new Scene(root, 1200, 800);
			scene.getStylesheets().add("/eu/dzim/filesyncfx/ui/application.css");
			
			primaryStage.setScene(scene);
			primaryStage.setOnCloseRequest(windowEvent -> {
				
				LOG.debug("tear down JavaFX application");
				// mApplicationModel.setLoggedIn(!mLoginService.logout());
				
				// orderly shut down FX
				Platform.exit();
				
				// But: there might still be a daemon thread left over from OkHttp (some async dispatcher)
				// so assume everything is fine and call System.exit(0)
				System.exit(0);
			});
			
			primaryStage.show();
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		// SpringApplication.run(SampleSimpleApplication.class, args);
		
		savedArgs = args;
		Application.launch(FXApplication.class, args);
	}
}
