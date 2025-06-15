package osiris.tracing

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger: KLogger = KotlinLogging.logger {}

public object EventLogger : Listener {
  override fun event(event: Event) {
    logger.info { "Osiris event: $event." }
  }

  override fun flush(): Unit = Unit
}
