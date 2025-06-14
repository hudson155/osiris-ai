package osiris.event

import dev.langchain4j.data.message.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

/**
 * A new [ChatMessage] has been created.
 */
public data class MessageEvent(
  val message: ChatMessage,
) : Event()

public fun Flow<Event>.onMessage(block: suspend (message: ChatMessage) -> Unit): Flow<Event> =
  onEach { event ->
    if (event !is MessageEvent) return@onEach
    block(event.message)
  }
