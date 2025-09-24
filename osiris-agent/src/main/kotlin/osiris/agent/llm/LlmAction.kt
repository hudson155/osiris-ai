package osiris.agent.llm

import dev.langchain4j.data.message.ChatMessage

internal enum class LlmAction {
  Greet,
  Llm,
  ;

  internal companion object {
    fun fromHistory(history: List<ChatMessage>): LlmAction {
      if (history.isEmpty()) return Greet
      return Llm
    }
  }
}
