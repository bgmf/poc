package eu.dzim.tests.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainRoot extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        stage.setTitle("Window");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Root.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1200, 800);

        // scene.setFill(Color.TRANSPARENT);
        // stage.initStyle(StageStyle.TRANSPARENT);

        scene.getStylesheets().add("/fxml/application.css");

        // Stage two = new Stage();
        // two.setTitle("two");
        // FXMLLoader loader2 = new FXMLLoader(getClass().getResource("TableTest2.fxml"));
        // Parent root2 = loader2.load();
        // Scene scene2 = new Scene(root2, 1200, 800);
        // two.setScene(scene2);
        // two.show();

        stage.setScene(scene);
        stage.show();
    }
}
