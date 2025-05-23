package osiris.core

import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import osiris.core.event.ExceptionOsirisEvent
import osiris.core.event.OsirisEvent
import osiris.core.event.ResponseOsirisEvent
import osiris.core.responseConverter.OsirisResponseType

private val logger: KLogger = KotlinLogging.logger {}

public class Osiris<out Response : Any>(
  private val model: ChatModel,
  private val responseType: OsirisResponseType<Response>,
) {
  public fun request(langchainRequest: ChatRequest): Flow<OsirisEvent<Response>> =
    channelFlow {
      logger.info { "Osiris is executing. $model." }
      try {
        val langchainResponse = withContext(Dispatchers.IO) {
          return@withContext model.chat(langchainRequest)
        }
        val osirisResponse = responseType.convert(langchainResponse)
        send(ResponseOsirisEvent(content = osirisResponse))
      } catch (e: Throwable) {
        logger.warn(e) { "An exception was thrown." }
        send(ExceptionOsirisEvent(e))
      }
    }

  public companion object {
    public fun create(
      model: ChatModel,
      block: OsirisBuilder<String>.() -> Unit = {},
    ): Osiris<String> =
      OsirisBuilder<String>(model).apply(block).build()
  }

  public fun <Response : Any> create(
    model: ChatModel,
    block: OsirisBuilder<Response>.() -> Unit,
  ): Osiris<Response> =
    OsirisBuilder<Response>(model).apply(block).build()
}
