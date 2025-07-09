package osiris.chat

import dev.langchain4j.data.message.ChatMessage

public data class LlmState(
  val consecutiveResponseTries: Int,
  val response: List<ChatMessage>,
) {
  public constructor() : this(
    consecutiveResponseTries = 0,
    response = emptyList(),
  )
}
