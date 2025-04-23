package osiris.osiris

import dev.langchain4j.model.chat.request.ChatRequest
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import osiris.osiris.event.ExceptionOsirisEvent
import osiris.osiris.event.OsirisEvent
import osiris.osiris.event.ResponseOsirisEvent
import osiris.osiris.responseConverter.OsirisResponseType

private val logger: KLogger = KotlinLogging.logger {}

public class Osiris<out Response : Any>(
  private val model: OsirisModel,
  private val responseType: OsirisResponseType<Response>,
) {
  public fun request(langchainRequest: ChatRequest): Flow<OsirisEvent<Response>> =
    channelFlow {
      logger.info { "Osiris is executing. $model." }
      try {
        val langchainResponse = model.request(langchainRequest)
        val osirisResponse = responseType.convert(langchainResponse)
        send(ResponseOsirisEvent(content = osirisResponse))
      } catch (e: Throwable) {
        logger.warn(e) { "An exception was thrown." }
        send(ExceptionOsirisEvent(e))
      }
    }

  public companion object {
    public fun <Response : Any> create(
      model: OsirisModel,
      block: OsirisBuilder<Response>.() -> Unit = {},
    ): Osiris<Response> =
      OsirisBuilder<Response>(model).apply(block).build()
  }
}
