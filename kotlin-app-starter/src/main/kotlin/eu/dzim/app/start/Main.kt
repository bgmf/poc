package eu.dzim.app.start

import com.google.inject.Guice
import com.google.inject.Injector
import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.stage.Stage
import java.io.File

class StarterApplication : Application() {

    private lateinit var injector: Injector

    override fun init() {
        super.init()
        logger.info("Init Starter Application")
        injector = Guice.createInjector(INJECTION_STAGE, GuiceModule())
    }

    override fun start(stage: Stage) {

        stage.apply {

            minWidth = 640.0
            minHeight = 480.0
            title = "FX"

            scene = VBox().apply {

                var counter = 0L

                val label = Label().also { l ->
                    l.text = "counter=$counter"
                }

                children.addAll(label, Button().also { b ->
                    b.text = "Increase Counter"
                    b.setOnAction {
                        counter++
                        label.text = "counter=$counter"
                    }
                })

            }.let { root ->
                Scene(root, minWidth, minHeight)
            }

        }.show()
    }

    override fun stop() {
        super.stop()
        logger.info("Stop Starter Application")
    }

    companion object {
        val INJECTION_STAGE = com.google.inject.Stage.DEVELOPMENT
        val logger by lazy { getLogger() }
    }
}

fun main(args: Array<String>) {

    val logger = getLogger("eu.dzim.app.start.MainKt")

    val config = File("./app-config.conf")
            .let { if (it.isFile) it else null }
            ?.let { ConfigFactory.parseFile(it) }
            ?.let { it.extract<Configuration>("configuration") }
            ?: Configuration()

    if (config.endpoint.url.isEmpty())
        logger.warn("No Endpoint specified!")

    var showAdminUI: Boolean = args.contains("-admin")
    showAdminUI = true // for now force admin UI

    if (showAdminUI)
        Application.launch(StarterApplication::class.java)
}