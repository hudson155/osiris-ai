package osiris.osiris

import com.openai.client.OpenAIClientAsync
import com.openai.models.ChatCompletionMessageParam
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

private val logger: KLogger = KotlinLogging.logger {}

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
