package eu.dzim.test.kotlin

import eu.dzim.shared.resource.Resource
import eu.dzim.test.model.TestModel
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import tornadofx.*

class TornadoView : View() {

    private val resource: Resource by di()
    private val model: TestModel by di()

    override val root = borderpane {
        center = vbox {
            region { vgrow = Priority.ALWAYS }
            hbox {
                label("Resource Binding: ")
                label { textProperty().bind(resource.getBinding("test.string")) }
            }

            region { vgrow = Priority.ALWAYS }
        }
        bottom = hbox {
            region { vgrow = Priority.ALWAYS }
            label("Model Binding: ")
            label { textProperty().bind(model.testStringProperty()) }
            region { vgrow = Priority.ALWAYS }
        }
    }

    // helper method: since we are not a full TornadoFX application (we display Tornado-Views in a non-Tornado basic Application class),
    // we need a helper function to access the root - otherwise, we get strange exceptions, which I probably need to ask the TornadoFX dev, why they
    // actually happen
    fun getView(): Pane = root
}