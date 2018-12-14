package eu.dzim.guice.fx.allinone;

import com.google.inject.Injector;
import eu.dzim.guice.fx.allinone.MainWithGuiceControllerFactory.FXMLLoaderService;
import eu.dzim.guice.fx.allinone.MainWithGuiceControllerFactory.MyInterface;
import javafx.fxml.FXML;

import javax.inject.Inject;

public class GuicyController {

    @Inject private FXMLLoaderService fxmlLoaderService;

    @Inject private MyInterface myInterfaceInstance;

    @Inject private Injector injector;

    @FXML
    protected void initialize() {

        System.out.println("The Guive Injector: " + injector);
        System.out.println("FXMLLoaderService: " + fxmlLoaderService);

        myInterfaceInstance.doSomething();
    }
}
