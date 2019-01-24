package eu.dzim.test.kotlin

import eu.dzim.test.model.TestModel
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage

class FXApplication : Application() {

    override fun start(primaryStage: Stage?) {

        val model = TestModel().apply {
            testString = "test"
        }.run {
            println(testString)
            println(testStringProperty().get())
        }

        primaryStage?.apply {
            minWidth = 640.0
            minHeight = 480.0
            title = "FX"

            scene = Pane().let {
                // init pane
                // transform it into a scene
                Scene(it) // .apply { more stuff }
            }
        }?.show()
    }
}