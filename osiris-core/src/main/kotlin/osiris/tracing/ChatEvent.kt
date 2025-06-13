package osiris.tracing

import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.response.ChatResponse

public data class ChatEvent(
  val request: ChatRequest,
  val response: ChatResponse,
) : Span.Details()
