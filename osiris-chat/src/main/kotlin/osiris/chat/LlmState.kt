package osiris.chat

import dev.langchain4j.data.message.ChatMessage

public data class LlmState internal constructor(
  val consecutiveResponseTries: Int,
  val response: List<ChatMessage>,
) {
  internal constructor() : this(
    consecutiveResponseTries = 0,
    response = emptyList(),
  )
}
