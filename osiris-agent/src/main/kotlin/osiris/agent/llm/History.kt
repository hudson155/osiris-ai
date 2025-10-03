package osiris.agent.llm

import dev.langchain4j.data.message.ChatMessage
import io.ktor.util.AttributeKey
import osiris.agent.Context

public abstract class History {
  public abstract suspend fun get(): List<ChatMessage>

  public abstract suspend fun append(message: ChatMessage)
}

private val key: AttributeKey<History> = AttributeKey("history")

public var Context.history: History
  get() = computeIfAbsent(key) { InMemoryHistory() }
  set(value) {
    put(key, value)
  }
