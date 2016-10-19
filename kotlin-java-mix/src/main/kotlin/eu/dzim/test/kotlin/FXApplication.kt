package eu.dzim.test.kotlin

import javafx.application.Application
import javafx.stage.Stage
import javafx.scene.layout.Pane
import javafx.scene.Scene
import eu.dzim.test.model.TestModel

class FXApplication : Application() {
	
	override fun start(primaryStage: Stage?) {
		
		var tm = TestModel()
		tm.testString = "test"
		println(tm.testString)
		println(tm.testStringProperty().get())
		
		primaryStage!!.minWidth = 640.0
		primaryStage.minHeight = 480.0
		primaryStage.title = "FX"
		
		var pane = Pane()
		
		var scene = Scene(pane)
		
		primaryStage.scene = scene
		
		primaryStage.show()
	}
}