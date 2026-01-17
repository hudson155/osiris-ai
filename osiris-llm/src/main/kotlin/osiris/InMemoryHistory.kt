package osiris

import dev.langchain4j.data.message.ChatMessage
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

public class InMemoryHistory : History() {
  private val mutex: Mutex = Mutex()
  private val messages: MutableList<ChatMessage> = mutableListOf()

  context(context: Context)
  override suspend fun get(): List<ChatMessage> =
    mutex.withLock { this.messages.toList() } // Create a copy of the list.

  context(context: Context)
  override suspend fun append(messages: List<ChatMessage>) {
    mutex.withLock { this.messages += messages }
  }
}
