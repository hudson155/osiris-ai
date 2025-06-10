package osiris.event

import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.response.ChatResponse

public sealed class ChatEvent : Event() {
  public abstract val request: ChatRequest

  public data class Start(
    override val request: ChatRequest,
  ) : ChatEvent()

  public data class End(
    override val request: ChatRequest,
    val response: ChatResponse,
  ) : ChatEvent()
}
