package osiris

import dev.langchain4j.data.message.ChatMessage
import io.ktor.util.AttributeKey

public abstract class History {
  context(context: Context)
  public abstract suspend fun get(): List<ChatMessage>

  context(context: Context)
  public suspend fun append(vararg message: ChatMessage) {
    append(message.toList())
  }

  context(context: Context)
  public abstract suspend fun append(messages: List<ChatMessage>)
}

private val key: AttributeKey<History> = AttributeKey("history")

public var Context.history: History
  get() = attributes.computeIfAbsent(key) { InMemoryHistory() }
  set(value) {
    attributes[key] = value
  }
