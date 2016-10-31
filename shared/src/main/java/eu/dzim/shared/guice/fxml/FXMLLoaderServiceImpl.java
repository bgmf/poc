package eu.dzim.shared.guice.fxml;

import java.net.URL;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;

import eu.dzim.shared.fx.fxml.FXMLLoaderService;
import eu.dzim.shared.fx.util.UIComponentType;
import javafx.fxml.FXMLLoader;

public class FXMLLoaderServiceImpl implements FXMLLoaderService {
	
	private static final Logger LOG = LogManager.getLogger(FXMLLoaderServiceImpl.class);
	
	@Inject private Injector injector;
	
	private ClassLoader otherClassLoader = null;
	
	public FXMLLoaderServiceImpl() {
		// sonar
	}
	
	public void setOtherClassLoader(ClassLoader otherClassLoader) {
		this.otherClassLoader = otherClassLoader;
	}
	
	// post-construct not working out of the box in Guice
	@PostConstruct
	private void postConstruct() {}
	
	@Override
	public FXMLLoader getLoader() {
		FXMLLoader loader = new FXMLLoader();
		if (this.otherClassLoader != null) {
			loader.setClassLoader(this.otherClassLoader);
		}
		loader.setControllerFactory(this::controllerForClass);
		return loader;
	}
	
	@Override
	public FXMLLoader getLoader(URL location) {
		FXMLLoader loader = new FXMLLoader(location);
		if (this.otherClassLoader != null) {
			loader.setClassLoader(this.otherClassLoader);
		}
		loader.setControllerFactory(this::controllerForClass);
		return loader;
	}
	
	@Override
	public FXMLLoader getLoader(UIComponentType component) {
		if (this.otherClassLoader != null) {
			URL resourceUrl = this.otherClassLoader.getResource(component.getAbsoluteLocation());
			if (resourceUrl == null && component.getAbsoluteLocation().startsWith("/"))
				resourceUrl = this.otherClassLoader.getResource(component.getAbsoluteLocation().substring(1));
			if (resourceUrl == null)
				LOG.error("Could not find resource for UIComponent '{}'!", component.getAbsoluteLocation());
			return getLoader(resourceUrl);
		}
		return getLoader(getClass().getResource(component.getAbsoluteLocation()));
	}
	
	@Override
	public FXMLLoader getLoader(URL location, ResourceBundle resourceBundle) {
		FXMLLoader loader = new FXMLLoader(location, resourceBundle);
		if (this.otherClassLoader != null) {
			loader.setClassLoader(this.otherClassLoader);
		}
		loader.setControllerFactory(this::controllerForClass);
		return loader;
	}
	
	@Override
	public FXMLLoader getLoader(UIComponentType component, ResourceBundle resourceBundle) {
		if (this.otherClassLoader != null) {
			URL resourceUrl = this.otherClassLoader.getResource(component.getAbsoluteLocation());
			if (resourceUrl == null && component.getAbsoluteLocation().startsWith("/"))
				resourceUrl = this.otherClassLoader.getResource(component.getAbsoluteLocation().substring(1));
			if (resourceUrl == null)
				LOG.error("Could not find resource for UIComponent '{}'!", component.getAbsoluteLocation());
			return getLoader(resourceUrl);
		}
		return getLoader(getClass().getResource(component.getAbsoluteLocation()), resourceBundle);
	}
	
	private Object controllerForClass(Class<?> clazz) {
		try {
			Object controllerInstance = injector.getInstance(clazz);
			// controllerInstance#someArbitraryMethodCall if necessary
			// will be triggered *before* the initialize method!
			return controllerInstance;
		} catch (ConfigurationException | ProvisionException e) {
			LOG.error(e);
			return null;
		}
	}
	
	// pre-destroy not working out of the box in Guice
	@PreDestroy
	private void preDestroy() {}
}
