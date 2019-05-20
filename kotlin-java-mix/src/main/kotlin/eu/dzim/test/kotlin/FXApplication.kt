package eu.dzim.test.kotlin

import com.google.inject.Guice
import eu.dzim.test.model.TestModel
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import tornadofx.*
import kotlin.reflect.KClass

class FXApplication : Application() {

    override fun start(primaryStage: Stage) {

        val model = TestModel().apply {
            testString = "test"
        }.also {
            println(it.testString)
            println(it.testStringProperty().get())
        }

        Guice.createInjector(INJECTION_STAGE, GuiceModule(model)).apply {
            FX.dicontainer = object : DIContainer {
                override fun <T : Any> getInstance(type: KClass<T>): T = getInstance(type.java)
            }
        }

        primaryStage.apply {
            minWidth = 640.0
            minHeight = 480.0
            title = "FX"

            scene = BorderPane().let {
                // init pane
                it.center = TornadoView().getView()
                // transform it into a scene
                Scene(it, minWidth, minHeight) // .apply { more stuff }
            }
        }.show()
    }
}