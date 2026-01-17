package osiris

import dev.langchain4j.data.message.ChatMessage
import io.ktor.util.AttributeKey

/**
 * Stores the canonical [ChatMessage] LLM history.
 * The default history implementation is [InMemoryHistory],
 * but this is insufficient for production use cases where persistent history is required.
 *
 * Implementations must be coroutine-safe.
 */
public abstract class History {
  /**
   * Retrieve the entire history.
   */
  context(context: Context)
  public abstract suspend fun get(): List<ChatMessage>

  /**
   * Append to the history.
   */
  context(context: Context)
  public suspend fun append(vararg message: ChatMessage) {
    append(message.toList())
  }

  /**
   * Append to the history.
   */
  context(context: Context)
  public abstract suspend fun append(messages: List<ChatMessage>)
}

private val key: AttributeKey<History> = AttributeKey("history")

public var Context.history: History
  get() = attributes.computeIfAbsent(key) { InMemoryHistory() }
  set(value) {
    attributes[key] = value
  }
