package osiris.event

import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.response.ChatResponse
import java.time.Instant

public sealed class ChatEvent : Event() {
  public data class Start(
    override val at: Instant,
    val request: ChatRequest,
  ) : ChatEvent() {
    @Suppress("ForbiddenMethodCall")
    public constructor(request: ChatRequest) : this(
      at = Instant.now(),
      request = request,
    )
  }

  public data class End(
    override val at: Instant,
    val response: ChatResponse,
  ) : ChatEvent() {
    @Suppress("ForbiddenMethodCall")
    public constructor(response: ChatResponse) : this(
      at = Instant.now(),
      response = response,
    )
  }
}
