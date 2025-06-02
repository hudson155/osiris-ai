package osiris.testing

import dev.langchain4j.data.message.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import osiris.core.OsirisEvent

public suspend fun Flow<OsirisEvent>.getMessages(): List<ChatMessage> =
  filterIsInstance<OsirisEvent.Message>().map { it.message }.toList()
