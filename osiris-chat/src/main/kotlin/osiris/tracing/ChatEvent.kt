package osiris.tracing

import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.response.ChatResponse

/**
 * Each separate request to the LLM will have a span.
 */
public object ChatEvent {
  public data class Start(
    val request: ChatRequest,
  ) : Event.Details()

  public data class End(
    val response: ChatResponse,
  ) : Event.Details()
}
