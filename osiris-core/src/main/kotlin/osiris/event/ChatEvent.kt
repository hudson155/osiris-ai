package osiris.event

import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.response.ChatResponse
import java.time.Instant

public sealed class ChatEvent : Event() {
  public abstract val request: ChatRequest

  public data class Start(
    val at: Instant,
    override val request: ChatRequest,
  ) : ChatEvent() {
    @Suppress("ForbiddenMethodCall")
    public constructor(request: ChatRequest) : this(
      at = Instant.now(),
      request = request,
    )
  }

  public data class End(
    val at: Instant,
    override val request: ChatRequest,
    val response: ChatResponse,
  ) : ChatEvent() {
    @Suppress("ForbiddenMethodCall")
    public constructor(request: ChatRequest, response: ChatResponse) : this(
      at = Instant.now(),
      request = request,
      response = response,
    )
  }
}
