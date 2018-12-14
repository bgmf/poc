package eu.dzim.tests.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainPdf extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        stage.setTitle("Window");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PDFTest.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1200, 800);

        stage.setScene(scene);
        stage.show();
    }
}
