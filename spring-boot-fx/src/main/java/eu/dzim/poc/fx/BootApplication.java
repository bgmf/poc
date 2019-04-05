package eu.dzim.poc.fx;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BootApplication {

    public static void main(String[] args) {
        Application.launch(FXApplication.class, args);
    }
}
