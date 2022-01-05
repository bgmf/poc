package eu.dzim.app.start

import com.google.inject.AbstractModule
import eu.dzim.shared.resource.BaseResource
import eu.dzim.shared.resource.Resource
import java.util.*

private const val DEFAULT_ENDPOINT = ""

data class Endpoint(var url: String)
data class Configuration(val endpoint: Endpoint = Endpoint(""))

class ResourceImpl : BaseResource("i18n", "strings", Locale.ENGLISH)

// could use eu.dzim.shared.guice.BaseGuiceModule instead
class GuiceModule : AbstractModule() {

    private val resource = ResourceImpl()

    override fun configure() {
        logger.info("Configure Guice.")
        bind(Resource::class.java).toInstance(resource)
    }

    companion object {
        private val logger by lazy { getLogger() }
    }
}

