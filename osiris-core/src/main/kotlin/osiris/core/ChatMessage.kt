package osiris.core

import dev.langchain4j.data.message.AiMessage
import kairo.serialization.util.readValueSpecial
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import osiris.event.Event
import osiris.event.MessageEvent

public suspend fun Flow<Event>.get(): AiMessage =
  this
    .filterIsInstance<MessageEvent>()
    .map { it.message }
    .filterIsInstance<AiMessage>()
    .first { !it.hasToolExecutionRequests() }

public inline fun <reified Response : Any> AiMessage.convert(): Response? =
  text()?.let { llmMapper.readValueSpecial<Response>(it) }
