package eu.dzim.shared.fx.util;

import eu.dzim.shared.fx.fxml.FXMLLoaderService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.util.Pair;

import javax.inject.Inject;

public class FXMLLoaderWrapperService extends Service<Pair<UIComponentType, Parent>> {

    private final ObjectProperty<UIComponentType> component = new SimpleObjectProperty<>();
    @Inject private FXMLLoaderService fxmlLoaderService;

    public FXMLLoaderWrapperService() {
        // for sonar
    }

    public ObjectProperty<UIComponentType> componentProperty() {
        return component;
    }

    @Override
    protected Task<Pair<UIComponentType, Parent>> createTask() {
        return new FXMLLoaderWrapperTask(fxmlLoaderService, component.get());
    }
}