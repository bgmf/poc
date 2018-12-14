package eu.dzim.poc.fx.service.impl;

import eu.dzim.poc.fx.service.FXMLLoaderService;
import javafx.fxml.FXMLLoader;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.URL;
import java.util.ResourceBundle;

@Component
@Scope("singleton")
public class FXMLLoaderServiceImpl implements FXMLLoaderService {

    private static final Logger LOG = LogManager.getLogger(FXMLLoaderServiceImpl.class);

    @Autowired private ConfigurableApplicationContext context;

    @PostConstruct
    private void postConstruct() {
        LOG.debug("PostConstruct: set up " + getClass().getName());
    }

    @Override
    public FXMLLoader getLoader() {
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> param) {
                return context.getBean(param);
            }
        });
        return loader;
    }

    @Override
    public FXMLLoader getLoader(URL location) {
        FXMLLoader loader = new FXMLLoader(location);
        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> param) {
                return context.getBean(param);
            }
        });
        return loader;
    }

    @Override
    public FXMLLoader getLoader(URL location, ResourceBundle resourceBundle) {
        FXMLLoader loader = new FXMLLoader(location, resourceBundle);
        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> param) {
                return context.getBean(param);
            }
        });
        return loader;
    }

    @PreDestroy
    private void preDestroy() {
        LOG.debug("PreDestroy: tear down " + getClass().getName());
    }
}
