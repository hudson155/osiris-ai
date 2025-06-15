package osiris.tracing

import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.response.ChatResponse

public object ChatEvent {
  public data class Start(
    val request: ChatRequest,
  ) : Event.Details()

  public data class End(
    val response: ChatResponse,
  ) : Event.Details()
}
