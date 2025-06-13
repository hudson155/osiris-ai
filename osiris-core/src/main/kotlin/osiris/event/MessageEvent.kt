package osiris.event

import dev.langchain4j.data.message.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

public data class MessageEvent(
  val message: ChatMessage,
) : Event()

public val Flow<Event>.messages
  get() = filterIsInstance<MessageEvent>().map { it.message }
