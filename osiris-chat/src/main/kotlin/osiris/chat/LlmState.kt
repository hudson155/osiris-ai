package osiris.chat

import dev.langchain4j.data.message.ChatMessage

public data class LlmState(
  val chatRequestCount: Int,
  val response: List<ChatMessage>,
) {
  public constructor() : this(
    chatRequestCount = 0,
    response = emptyList(),
  )
}
