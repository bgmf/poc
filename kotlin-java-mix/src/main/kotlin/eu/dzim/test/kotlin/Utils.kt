package eu.dzim.test.kotlin

import com.google.inject.AbstractModule
import com.google.inject.Stage
import eu.dzim.shared.resource.BaseResource
import eu.dzim.shared.resource.Resource
import eu.dzim.test.model.TestModel
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.*

val INJECTION_STAGE = Stage.DEVELOPMENT

class ResourceImpl : BaseResource("i18n", "strings", Locale.ENGLISH)

// could use eu.dzim.shared.guice.BaseGuiceModule instead
class GuiceModule(private val testModel: TestModel) : AbstractModule() {

    private val resource = ResourceImpl()

    override fun configure() {
        logger.info("Configure Guice.")
        bind(Resource::class.java).toInstance(resource)
        bind(TestModel::class.java).toInstance(testModel)
    }

    companion object {
        private val logger by lazy { getLogger() }
    }
}

inline fun Any.getLogger(): Logger = LogManager.getLogger(this::class.qualifiedName)