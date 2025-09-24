package osiris.agent

import dev.langchain4j.data.message.ChatMessage

public abstract class HistoryInterface {
  public abstract suspend fun get(): List<ChatMessage>

  public abstract suspend fun append(message: ChatMessage)
}
