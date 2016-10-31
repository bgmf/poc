package eu.dzim.shared.fx.fxml;

import java.net.URL;
import java.util.ResourceBundle;

import eu.dzim.shared.fx.util.UIComponentType;
import javafx.fxml.FXMLLoader;

public interface FXMLLoaderService {
	
	FXMLLoader getLoader();
	
	FXMLLoader getLoader(URL location);
	
	FXMLLoader getLoader(UIComponentType component);
	
	FXMLLoader getLoader(URL location, ResourceBundle resourceBundle);
	
	FXMLLoader getLoader(UIComponentType component, ResourceBundle resourceBundle);
}
