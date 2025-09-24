package osiris.agent

import dev.langchain4j.data.message.ChatMessage

public interface LlmContext {
  public suspend fun getHistory(): List<ChatMessage>
}
