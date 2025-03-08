package osiris.osiris

import com.openai.client.OpenAIClientAsync
import com.openai.models.ChatCompletionMessageParam
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

private val logger: KLogger = KotlinLogging.logger {}

/**
 * The main interface for interacting with Osiris.
 * All interaction is asynchronous, and uses [Flow]s to send [OsirisEvent]s back to the caller.
 * The flow is cold.
 *
 * Performs best when you create a single instance and reuse it for all interactions.
 * [OpenAIClientAsync] holds its own connection pool and thread pools.
 * Reusing this class reduces latency and saves memory.
 */
public class Osiris(
  private val openAi: OpenAIClientAsync,
) {
  public fun <Response : Any> execute(
    messages: List<ChatCompletionMessageParam>,
    options: OsirisOptions<Response>,
  ): Flow<OsirisEvent<Response>> =
    channelFlow {
      logger.info { "Osiris is executing." }
      with(OsirisExecution(openAi, messages, options)) {
        execute()
      }
    }
}
