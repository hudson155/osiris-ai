package osiris.agentic

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger: KLogger = KotlinLogging.logger {}

internal fun logEvent(event: Event) {
  logger.info { "Event: $event." }
}
