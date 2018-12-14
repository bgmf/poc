package eu.dzim.guice.fx.allinone;

import com.google.inject.*;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class MainWithGuiceControllerFactory extends Application {

    final com.google.inject.Stage injectionStage = com.google.inject.Stage.DEVELOPMENT;
    final Injector injector = Guice.createInjector(injectionStage, new MyModule());

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // instead of
        // FXMLLoader loader = new FXMLLoader(getClass().getResource("GuicyTest.fxml"));
        // you would fetch a FXMLLoader from the respective service, but first you need the service itself
        FXMLLoaderService loaderService = injector.getInstance(FXMLLoaderService.class);
        // get the real loader
        FXMLLoader loader = loaderService.getLoader(getClass().getResource("/fxml/allinone/GuicyTest.fxml"));
        // load the content and set it to the scene
        Parent root = loader.load();
        Scene scene = new Scene(root, 1200, 800);
        // ignore these two lines
        // scene.setFill(Color.TRANSPARENT);
        // stage.initStyle(StageStyle.TRANSPARENT);
        // show the stuff
        stage.setScene(scene);
        stage.show();
    }

    public static interface MyInterface {
        void doSomething();
    }

    public interface FXMLLoaderService {
        FXMLLoader getLoader();

        FXMLLoader getLoader(URL location);

        FXMLLoader getLoader(URL location, ResourceBundle resourceBundle);
    }

    private static class MyInterfaceImplementation implements MyInterface {
        @Override
        public void doSomething() {
            System.out.println("It works!");
        }
    }

    private static class FXMLLoaderServiceImpl implements FXMLLoaderService {

        @Inject private Injector injector;

        @PostConstruct
        private void postConstruct() {
        }

        @Override
        public FXMLLoader getLoader() {
            FXMLLoader loader = new FXMLLoader();
            loader.setControllerFactory(this::controllerForClass);
            return loader;
        }

        @Override
        public FXMLLoader getLoader(URL location) {
            FXMLLoader loader = new FXMLLoader(location);
            loader.setControllerFactory(this::controllerForClass);
            return loader;
        }

        @Override
        public FXMLLoader getLoader(URL location, ResourceBundle resourceBundle) {
            FXMLLoader loader = new FXMLLoader(location, resourceBundle);
            loader.setControllerFactory(this::controllerForClass);
            return loader;
        }

        private Object controllerForClass(Class<?> clazz) {
            try {
                Object controllerInstance = injector.getInstance(clazz);
                // controllerInstance#someArbitraryMethodCall if necessary
                // will be triggered *before* the initialize method!
                return controllerInstance;
            } catch (ConfigurationException | ProvisionException e) {
                e.printStackTrace();
                return null;
            }
        }

        @PreDestroy
        private void preDestroy() {
        }
    }

    // module - generally needed, once for the FXMLLoaderService and of course for any other service you would need
    private static class MyModule extends AbstractModule {

        private final MyInterface myInterfaceInstance = new MyInterfaceImplementation();
        private final FXMLLoaderService fxmlLoaderService = new FXMLLoaderServiceImpl();

        @Override
        protected void configure() {
            bind(FXMLLoaderService.class).toInstance(fxmlLoaderService);
            bind(MyInterface.class).toInstance(myInterfaceInstance);
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
