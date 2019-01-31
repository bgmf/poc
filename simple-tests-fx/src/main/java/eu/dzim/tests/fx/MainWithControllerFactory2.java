package eu.dzim.tests.fx;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainWithControllerFactory2 extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        // Premise: We have a single and shared model, that will be used across the application
        // our second UI (sub-UI of the first one) will get the model and increase a counter
        // the first UI, loaded via the FXMLLoader, will display the counters value
        Model model = new Model();

        // our loader get's the shiny controller factory

        // we could use the interface mechanism, but to make it a bit different then the other solutions, we use reflection black-magic
        // although neat, note, that I don't need to generify the controller factory

        // since I will use the FXMLs sub-view mechanism (see FactoryInjection.fxml), the loader is inherited,
        // so all directly defined sub-UIs will not need to specify their own FXMLLoader with controller factory

        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(c -> controllerForClassWithName(c, "setModel", model));
        loader.setLocation(getClass().getResource("/fxml/FactoryInjection.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 200, 100);

        stage.setScene(scene);
        stage.show();
    }

    // private Object controllerForClassWithInterface(Class<?> clazz, Model model) {
    //     try {
    //         Object controllerInstance = clazz.newInstance();
    //
    //         if (ModelInjector.class.isAssignableFrom(controllerInstance.getClass()))
    //             ((ModelInjector) controllerInstance).setModel(model);
    //
    //         return controllerInstance;
    //     } catch (InstantiationException | IllegalAccessException e) {
    //         e.printStackTrace();
    //         return null;
    //     }
    // }

    private Object controllerForClassWithName(Class<?> clazz, String inheritedMethodName, Object data) {
        try {
            Object controllerInstance = clazz.newInstance();

            try {
                Method method = clazz.getDeclaredMethod(inheritedMethodName, data.getClass());
                boolean accessible = method.isAccessible();
                method.setAccessible(true);
                method.invoke(controllerInstance, data);
                method.setAccessible(accessible);
            } catch (NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }

            return controllerInstance;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public final class Model {

        private final IntegerProperty counter;

        public Model() {
            counter = new SimpleIntegerProperty(this, "counter", 0);
        }

        public int getCounter() {
            return counter.get();
        }

        public IntegerProperty counterProperty() {
            return counter;
        }

        public void setCounter(int counter) {
            this.counter.set(counter);
        }

        public void increaseCounter() {
            counter.set(counter.get() + 1);
        }
    }

    public interface ModelInjector {
        void setModel(Model model);
    }
}

