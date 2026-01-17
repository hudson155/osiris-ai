package osiris

import dev.langchain4j.data.message.ChatMessage
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Default implementation of [History], storing [ChatMessage]s in memory.
 * This implementation is insufficient for production use cases where persistent history is required.
 */
public class InMemoryHistory : History() {
  private val mutex: Mutex = Mutex() // For coroutine safety.
  private val messages: MutableList<ChatMessage> = mutableListOf()

  context(context: Context)
  override suspend fun get(): List<ChatMessage> =
    mutex.withLock { this.messages.toList() } // Create a copy of the list.

  context(context: Context)
  override suspend fun lastOrNull(): ChatMessage? =
    mutex.withLock { this.messages.lastOrNull() }

  context(context: Context)
  override suspend fun append(messages: List<ChatMessage>) {
    mutex.withLock { this.messages += messages }
  }
}
