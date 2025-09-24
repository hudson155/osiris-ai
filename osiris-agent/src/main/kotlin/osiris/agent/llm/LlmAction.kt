package osiris.agent.llm

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.UserMessage

internal enum class LlmAction {
  Greet,
  Llm,
  WaitingForUser
  ;

  internal companion object {
    fun fromHistory(history: List<ChatMessage>): LlmAction {
      if (history.isEmpty()) return Greet
      return when (val lastMessage = history.last()) {
        is AiMessage -> WaitingForUser
        is UserMessage -> Llm
        else -> error("Unsupported message type (type=${lastMessage::class}).")
      }
    }
  }
}
