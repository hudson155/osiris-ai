package osiris.chat

import dev.langchain4j.data.message.ChatMessage

public data class LlmState(
  val response: List<ChatMessage>,
)
