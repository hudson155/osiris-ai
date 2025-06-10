package osiris.event

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.UserMessage
import java.time.Instant

public sealed class ExecutionEvent : Event() {
  public data class Start(
    val at: Instant,
    val entrypoint: String,
    val input: String?,
  ) : ExecutionEvent() {
    @Suppress("ForbiddenMethodCall")
    public constructor(
      entrypoint: String,
      messages: List<ChatMessage>,
    ) : this(
      at = Instant.now(),
      entrypoint = entrypoint,
      input = (messages.last() as? UserMessage)?.singleText(),
    )
  }

  public data class End(
    val at: Instant,
    val output: String?,
  ) : ExecutionEvent() {
    @Suppress("ForbiddenMethodCall")
    public constructor(output: String) : this(
      at = Instant.now(),
      output = output,
    )
  }
}
