package eu.dzim.app.start

import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window
import java.util.function.Supplier

internal class StageWrapper(
    style: StageStyle?, modality: Modality?, owner: Window?, minWidth: Double, minHeight: Double, width: Double, height: Double,
    title: String?, alwaysOnTop: Boolean
) : Stage(style) {
    private val lWidth: Double
    private val lHeight: Double
    private var sceneFill: Paint = Color.WHITE
    private var sceneContentSupplier = Supplier<Pane> { BorderPane() }

    init {
        initModality(modality)
        initOwner(owner)
        if (minWidth > 0) setMinWidth(minWidth)
        if (minHeight > 0) setMinHeight(minHeight)
        this.lWidth = width
        this.lHeight = height
        setTitle(title)
        isAlwaysOnTop = alwaysOnTop
    }

    fun setSceneFill(sceneFill: Paint) {
        this.sceneFill = sceneFill
    }

    fun setSceneContentSupplier(sceneContentSupplier: Supplier<Pane>) {
        this.sceneContentSupplier = sceneContentSupplier
        createContent()
    }

    private fun createContent() {
        val content = sceneContentSupplier.get()
        val scene = Scene(content, lWidth, lHeight)
        scene.fill = sceneFill
        setScene(scene)
    }

    fun showContent(showAndWait: Boolean, sceneContentSupplier: Supplier<Pane>? = null) {
        sceneContentSupplier?.also { this.sceneContentSupplier = it }
        createContent()
        if (showAndWait) showAndWait() else show()
    }
}