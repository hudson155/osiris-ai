package osiris.tracing

import dev.langchain4j.data.message.ChatMessage

public object LlmEvent {
  public data class Start(
    val input: List<ChatMessage>,
  ) : Event.Details()

  public data class End(
    val output: List<ChatMessage>,
  ) : Event.Details()
}
