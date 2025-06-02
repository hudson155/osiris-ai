package osiris.testing

import dev.langchain4j.data.message.ChatMessage
import osiris.core.OsirisEvent

public val List<OsirisEvent>.response: String?
  get() = filterIsInstance<OsirisEvent.Response>().single().response

public val List<OsirisEvent>.messages: List<ChatMessage>
  get() = filterIsInstance<OsirisEvent.Message>().map { it.message }.toList()
