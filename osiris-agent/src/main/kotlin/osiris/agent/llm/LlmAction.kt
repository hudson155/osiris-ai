package osiris.agent.llm

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.ToolExecutionResultMessage
import dev.langchain4j.data.message.UserMessage

internal enum class LlmAction {
  Greet,
  Llm,
  Tools,
  User,
  ;

  internal companion object {
    fun fromHistory(history: List<ChatMessage>): LlmAction {
      if (history.isEmpty()) return Greet
      return when (val lastMessage = history.last()) {
        is AiMessage -> if (lastMessage.hasToolExecutionRequests()) Tools else User
        // TODO: If one of several parallel tool execution requests fails, this is the wrong action to take.
        is ToolExecutionResultMessage -> Llm
        is UserMessage -> Llm
        else -> error("Unsupported message type (type=${lastMessage::class}).")
      }
    }
  }
}
