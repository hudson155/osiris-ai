package osiris

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import io.ktor.util.AttributeKey
import kairo.reflect.KairoType
import kairo.reflect.kairoType

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
   * Retrieve the last message in the history, or null if the history is empty.
   */
  context(context: Context)
  public open suspend fun lastOrNull(): ChatMessage? =
    get().lastOrNull()

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

public suspend fun Context.getResponseText(): String {
  val aiMessage = history.lastOrNull() as AiMessage
  return aiMessage.text()
}

public suspend inline fun <reified T> Context.getResponseAs(): T =
  getResponseAs(kairoType())

@JvmName("getResponseAs")
public suspend fun <T> Context.getResponseAs(type: KairoType<T>): T =
  json.deserialize(getResponseText(), type)
