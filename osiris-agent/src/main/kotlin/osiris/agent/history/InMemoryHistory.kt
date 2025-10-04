package osiris.agent.history

import dev.langchain4j.data.message.ChatMessage
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

public class InMemoryHistory : History() {
  private val lock: ReentrantReadWriteLock = ReentrantReadWriteLock()
  private val messages: MutableList<ChatMessage> = mutableListOf()

  override suspend fun get(): List<ChatMessage> =
    lock.read { messages.toList() } // Create a copy of the list.

  override suspend fun append(message: ChatMessage) {
    lock.write { messages += message }
  }
}
