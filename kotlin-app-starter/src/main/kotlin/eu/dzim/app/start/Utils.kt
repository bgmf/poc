package eu.dzim.app.start

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

inline fun Any.getLogger(): Logger = LogManager.getLogger(this::class.qualifiedName)

inline fun getLogger(name: String): Logger = LogManager.getLogger(name)