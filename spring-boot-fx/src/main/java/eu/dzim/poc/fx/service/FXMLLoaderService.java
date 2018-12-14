package eu.dzim.poc.fx.service;

import javafx.fxml.FXMLLoader;

import java.net.URL;
import java.util.ResourceBundle;

public interface FXMLLoaderService {

    FXMLLoader getLoader();

    FXMLLoader getLoader(URL location);

    FXMLLoader getLoader(URL location, ResourceBundle resourceBundle);
}
