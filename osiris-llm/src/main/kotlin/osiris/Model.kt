package osiris

import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.response.ChatResponse
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.util.AttributeKey
import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext

private val logger: KLogger = KotlinLogging.logger {}

public class Model(
  private val model: ChatModel,
) {
  @Target(AnnotationTarget.VALUE_PARAMETER)
  public annotation class Id(val provider: String, val name: String)

  public suspend fun chat(block: suspend ChatRequest.Builder.() -> Unit): ChatResponse {
    val chatRequest = ChatRequest.builder().apply { block() }.build()
    logger.debug { "Chat request (chatRequest=$chatRequest)." }
    val chatResponse = async { model.chat(chatRequest) }
    logger.debug { "Chat response (chatResponse=$chatResponse)." }
    return chatResponse
  }

  private suspend fun <T> async(block: suspend () -> T): T =
    withContext(coroutineDispatcher) {
      block()
    }

  public companion object {
    private val coroutineDispatcher: CoroutineDispatcher =
      Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()
  }
}

private val key: AttributeKey<Model> = AttributeKey("model")

public var Context.defaultModel: Model?
  get() = attributes.getOrNull(key)
  set(value) {
    if (value != null) {
      attributes[key] = value
    } else {
      attributes.remove(key)
    }
  }
