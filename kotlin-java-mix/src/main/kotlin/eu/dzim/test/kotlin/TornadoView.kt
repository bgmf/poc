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

    fun getView(): Pane = root
}