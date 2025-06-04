package osiris.core

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import kairo.serialization.util.readValueSpecial
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first

public suspend fun Flow<ChatMessage>.get(): AiMessage =
  filterIsInstance<AiMessage>().first { !it.hasToolExecutionRequests() }

public inline fun <reified Response : Any> AiMessage.convert(): Response? =
  text()?.let { llmMapper.readValueSpecial<Response>(it) }
