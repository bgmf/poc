package eu.dzim.app.start

import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.embed.swing.SwingFXUtils
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.SnapshotParameters
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.StageStyle
import java.io.File
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO

fun main(args: Array<String>) {

    val logger = getLogger("eu.dzim.app.start.MainMultiAppKt")

    val config = File("./app-config.conf")
        .let { if (it.isFile) it else null }
        ?.let { ConfigFactory.parseFile(it) }
        ?.let { it.extract<Configuration>("configuration") }
        ?: Configuration()

    if (config.endpoint.url.isEmpty())
        logger.warn("No Endpoint specified!")

    var showAdminUI: Boolean = args.contains("-admin")
    showAdminUI = true // for now force admin UI

    var headless: Boolean = args.contains("-headless")
    headless = true

    if (headless) {
        /*
        -Dglass.platform=Monocle
        -Dmonocle.platform=Headless
        -Dprism.order=sw
        -Dprism.text=t2k
         */
        System.setProperty("glass.platform", "Monocle")
        System.setProperty("monocle.platform", "Headless")
        System.setProperty("prism.order", "sw")
        System.setProperty("prism.text", "t2k")
    }

    if (showAdminUI) {
        // init the ToolKit - can only be done once!
        JFXPanel()
        // prevent implicit exit of the TK - we can't restart it in JFX 11
        Platform.setImplicitExit(false)

        val options = mutableMapOf<String, String>()

        // main window
        fun getStageWrapper(): StageWrapper = StageWrapper(
            StageStyle.DECORATED,
            Modality.APPLICATION_MODAL,
            null,
            -1.0,
            -1.0,
            640.0,
            480.0,
            "Application",
            false,
        ).apply {
            setSceneContentSupplier {
                VBox().apply {

                    alignment = Pos.CENTER

                    var counter = 0L
                    fun Label.update() {
                        text = "${options["filename"]} counter=$counter"
                    }

                    val label = Label().apply { update() }
                    children.addAll(
                        label,
                        Button().apply {
                            text = "Increase Counter"
                            setOnAction { counter++; label.update() }
                        },
                    )
                }
            }
            onShown = EventHandler {
                logger.info("Window ${options["filename"]} is showing.")
                val params = SnapshotParameters()
                val image = scene.root.snapshot(params, null)
                logger.info("Window ${options["filename"]} image created.")
                try {
                    ImageIO.write(
                        SwingFXUtils.fromFXImage(image, null),
                        "png",
                        Path.of("./target/${options["filename"]}.png").toAbsolutePath().toFile()
                    )
                    logger.info("Window ${options["filename"]} image saved.")
                } catch (e: Exception) {
                    logger.warn("Image writing failed")
                } finally {
                    Thread {
                        TimeUnit.MILLISECONDS.sleep(500)
                        Platform.runLater {
                            logger.info("Window ${options["filename"]} will be hiding now.")
                            hide()
                        }
                    }.start()
                }
            }
            onHidden = EventHandler {
                logger.info("Window ${options["filename"]} is hidden.")
            }
        }

        Platform.runLater {
            options["filename"] = "test1"
            getStageWrapper().showContent(true)

            options["filename"] = "test2"
            getStageWrapper().showContent(true)

            try {
                Platform.exit()
            } catch (e: Exception) {
                // ignore all exceptions
            }
        }
    }
}