package osiris.agentic

import dev.langchain4j.data.message.ChatMessage

public data class NetworkState(
  val currentAgent: Agent,
  val messages: List<ChatMessage>,
)
