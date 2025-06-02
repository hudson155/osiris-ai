package osiris.testing

import dev.langchain4j.data.message.ChatMessage
import osiris.core.OsirisEvent

public fun <Response : Any> List<OsirisEvent<Response>>.getResponse(): Response? =
  filterIsInstance<OsirisEvent.Response<Response>>().single().response

public fun List<OsirisEvent<*>>.getMessages(): List<ChatMessage> =
  filterIsInstance<OsirisEvent.Message>().map { it.message }.toList()
