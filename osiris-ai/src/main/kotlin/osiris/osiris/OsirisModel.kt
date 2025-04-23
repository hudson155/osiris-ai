package osiris.osiris

import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.response.ChatResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

public class OsirisModel(
  public val name: String,
  private val model: ChatLanguageModel,
) {
  public suspend fun request(langchainRequest: ChatRequest): ChatResponse =
    withContext(Dispatchers.IO) {
      return@withContext model.chat(langchainRequest)
    }

  override fun toString(): String =
    "OsirisModel(name='$name')"
}
