package osiris.core

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import osiris.event.Event
import osiris.event.MessageEvent

public fun Flow<Event>.messages(): Flow<ChatMessage> =
  filterIsInstance<MessageEvent>().map { it.message }

public suspend fun Flow<Event>.response(): AiMessage =
  messages().last() as AiMessage
