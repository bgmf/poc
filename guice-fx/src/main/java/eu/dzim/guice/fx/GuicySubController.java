package eu.dzim.guice.fx;

import com.google.inject.Injector;
import eu.dzim.guice.fx.disposable.Disposable;
import eu.dzim.guice.fx.service.FXMLLoaderService;
import eu.dzim.guice.fx.service.MyInterface;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import javax.inject.Inject;

public class GuicySubController implements Disposable {

    @Inject private FXMLLoaderService fxmlLoaderService;

    @Inject private MyInterface myInterfaceInstance;

    @Inject private Injector injector;

    @FXML private Button button;

    @FXML
    protected void initialize() {
        System.out.println(this.getClass().getSimpleName() + ": The Guive Injector: " + injector);
        System.out.println(this.getClass().getSimpleName() + ": FXMLLoaderService: " + fxmlLoaderService);
    }

    @FXML
    public void handleButton(ActionEvent event) {
        myInterfaceInstance.doSomething();
    }

    @Override
    public void dispose() {
        System.out.println(this.getClass().getSimpleName() + ": dispose");
    }
}
