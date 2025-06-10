package osiris.event

import dev.langchain4j.data.message.ChatMessage

public data class ChatMessageEvent(
  val message: ChatMessage,
) : Event()
