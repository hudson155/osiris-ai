package osiris.event

import dev.langchain4j.data.message.ChatMessage

/**
 * A new [ChatMessage] has been created.
 */
public data class MessageEvent(
  val message: ChatMessage,
) : Event()
