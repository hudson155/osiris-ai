package osiris

import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.response.ChatResponse
import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext

public class Model(
  private val model: ChatModel,
) {
  public suspend fun chat(block: suspend ChatRequest.Builder.() -> Unit): ChatResponse {
    val aiRequest = ChatRequest.builder().apply { block() }.build()
    return async { model.chat(aiRequest) }
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
