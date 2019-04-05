package eu.dzim.poc.fx;

import eu.dzim.poc.fx.model.ApplicationModel;
import eu.dzim.poc.fx.resource.Resource;
import eu.dzim.poc.fx.service.FXMLLoaderService;
import eu.dzim.poc.fx.util.Utils;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

public class FXApplication extends Application {

    private static final Logger LOG = LogManager.getLogger(FXApplication.class);

    private ConfigurableApplicationContext applicationContext;

    @Autowired private FXMLLoaderService fxmlLoaderService;
    @Autowired private Resource mResource;
    @Autowired private ApplicationModel mApplicationModel;

    @Override
    public void init() throws Exception {

        // set Thread name
        Thread.currentThread().setName("main");

        LOG.debug("Init JavaFX application");

        /* new approach taken from:
           -> https://github.com/spring-tips/javafx
         */
        ApplicationContextInitializer<GenericApplicationContext> initializer = context -> {
            context.registerBean(Application.class, () -> FXApplication.this);
            context.registerBean(Parameters.class, this::getParameters);
            context.registerBean(HostServices.class, this::getHostServices);
        };
        applicationContext = new SpringApplicationBuilder().sources(BootApplication.class).initializers(initializer)
                .run(getParameters().getRaw().toArray(new String[0]));

        /* The old approach fails to initialize the application */
        // applicationContext = SpringApplication.run(getClass(), getParameters().getRaw().toArray(new String[0]));

        applicationContext.getAutowireCapableBeanFactory().autowireBean(this);
    }

    @Override
    public void start(Stage primaryStage) {

        // set Thread name
        Thread.currentThread().setName("main-ui");

        try {
            FXMLLoader loader = fxmlLoaderService.getLoader(Utils.getFXMLResource("Root"), mResource.getResourceBundle());

            Pane root = loader.load();

            Scene scene = new Scene(root, 1200, 800);
            // scene.getStylesheets().add("/eu/dzim/filesyncfx/ui/application.css");

            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(windowEvent -> {
                LOG.debug("tear down JavaFX application");
                // mApplicationModel.setLoggedIn(!mLoginService.logout());
            });

            primaryStage.show();

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public void stop() throws Exception {
        LOG.debug("Stop JavaFX application");
        super.stop();
        applicationContext.close();
        Platform.exit();
    }
}
