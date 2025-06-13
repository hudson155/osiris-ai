package osiris.event

import dev.langchain4j.data.message.ChatMessage

public data class MessageEvent(
  val message: ChatMessage,
) : Event()
