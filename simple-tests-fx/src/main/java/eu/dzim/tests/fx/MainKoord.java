package eu.dzim.tests.fx;

import eu.dzim.tests.fx.controller.FXMLDocumentController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainKoord extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Koordinatensystem");
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainKoord.class.getResource("/fxml/FXMLDocument.fxml"));
            Pane rootLayout = (Pane) loader.load();
            FXMLDocumentController controller = loader.getController();
            Scene scene = new Scene(rootLayout);
            controller.setSettings(scene);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}