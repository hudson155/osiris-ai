package osiris.core

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage

public fun List<ChatMessage>.getResponse(): String =
  (last() as AiMessage).text()
