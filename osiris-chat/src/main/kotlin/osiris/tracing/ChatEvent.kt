package osiris.tracing

import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.response.ChatResponse
import osiris.chat.deriveText

/**
 * Each separate request to the LLM will have a span.
 */
internal object ChatEvent {
  fun start(request: ChatRequest): Event.Start.Creator =
    Event.Start.Creator(
      type = "Chat",
      content = deriveText(request.messages()),
      properties = mapOf("request" to request),
    )

  fun end(response: ChatResponse): Event.End.Creator =
    Event.End.Creator(
      content = response.aiMessage()?.let { deriveText(it) },
      properties = mapOf("response" to response),
    )
}
