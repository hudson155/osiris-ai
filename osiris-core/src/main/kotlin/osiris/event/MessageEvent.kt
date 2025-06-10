package osiris.event

import dev.langchain4j.data.message.ChatMessage
import java.time.Instant

public data class MessageEvent(
  override val at: Instant,
  val message: ChatMessage,
) : Event() {
  @Suppress("ForbiddenMethodCall")
  public constructor(message: ChatMessage) : this(
    at = Instant.now(),
    message = message,
  )
}
