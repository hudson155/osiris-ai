package osiris.core

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import osiris.event.Event
import osiris.event.MessageEvent

/**
 * Helper to disregard everything from the resulting Flow except the chat messages.
 */
public fun Flow<Event>.messages(): Flow<ChatMessage> =
  filterIsInstance<MessageEvent>().map { it.message }

public fun List<Event>.messages(): List<ChatMessage> =
  filterIsInstance<MessageEvent>().map { it.message }

/**
 * Helper to disregard everything from the resulting Flow except the final chat message.
 *
 * This assumes the last message is an [AiMessage], which is true with [ExitCondition.Default].
 * If you customize the exit condition, this helper might not work.
 */
public suspend fun Flow<Event>.response(): AiMessage =
  messages().last() as AiMessage

public fun List<Event>.response(): AiMessage =
  messages().last() as AiMessage
