package osiris.tracing

import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.response.ChatResponse
import osiris.chat.deriveText

internal object ChatEvent {
  fun start(request: ChatRequest): Event.Start.Creator =
    Event.Start.Creator(
      type = "Chat",
      name = buildString {
        append("Chat")
        request.modelName()?.let { append(": $it") }
      },
      content = deriveText(request.messages()),
      properties = mapOf("request" to request),
    )

  fun end(response: ChatResponse): Event.End.Creator =
    Event.End.Creator(
      name = response.modelName()?.let { "Chat: $it" },
      content = response.aiMessage()?.let { deriveText(it) },
      properties = mapOf("response" to response),
    )
}
