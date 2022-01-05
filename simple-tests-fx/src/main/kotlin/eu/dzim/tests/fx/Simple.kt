package eu.dzim.tests.fx

import javafx.application.Application
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.stage.Stage

fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}

class Main : Application() {

    override fun start(primaryStage: Stage) {

        val model = Model().apply {
            setMyString("TEST")
        }

        primaryStage.apply {

            minWidth = 640.0
            minHeight = 480.0
            title = "FX"

            scene = VBox().apply {

                var counter = 0L

                val label1 = Label().also { l ->
                    l.textProperty().bind(model.myStringProperty())
                }
                val label2 = Label().also { l ->
                    l.text = "counter=$counter"
                }

                children.addAll(label1, label2, Button().also { b ->
                    b.text = "Increase Counter"
                    b.setOnAction {
                        counter++
                        label2.text = "counter=$counter"
                    }
                })

            }.let { root ->
                Scene(root, minWidth, minHeight)
            }

        }.show()
    }
}

private class Model {

    private val myString = SimpleStringProperty(this, "myString", "")

    fun getMyString(): String = myString.value

    fun setMyString(value: String) {
        myString.value = value
    }

    fun myStringProperty(): StringProperty = myString
}