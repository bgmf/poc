package eu.dzim.shared.fx.util;

import eu.dzim.shared.fx.fxml.FXMLLoaderService;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Pair;

public class FXMLLoaderWrapperTask extends Task<Pair<UIComponentType, Parent>> {

    private final FXMLLoaderService fxmlLoaderService;
    private final UIComponentType componentType;

    public FXMLLoaderWrapperTask(final FXMLLoaderService fxmlLoaderService, final UIComponentType componentType) {
        this.fxmlLoaderService = fxmlLoaderService;
        this.componentType = componentType;
    }

    @Override
    protected Pair<UIComponentType, Parent> call() throws Exception {
        FXMLLoader loader = fxmlLoaderService.getLoader(componentType);
        Parent content = loader.load();
        content.setUserData(loader.getController());
        return new Pair<>(componentType, content);
    }
}
