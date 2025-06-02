package osiris.core

import dev.langchain4j.data.message.ChatMessage

public sealed class OsirisEvent {
  public data class Message(val message: ChatMessage) : OsirisEvent()

  public data class Response(val response: String?) : OsirisEvent()
}
