package eu.dzim.tests.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainTableTest2 extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Koordinatensystem");
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainTableTest2.class.getResource("/fxml/TableTest2.fxml"));
            Pane rootLayout = (Pane) loader.load();
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}