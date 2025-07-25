package osiris.chat

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.UserMessage

public fun deriveText(messages: List<ChatMessage>): String? =
  messages.asReversed().firstNotNullOfOrNull { message ->
    when (message) {
      is AiMessage -> message.text()
      is UserMessage -> message.singleText()
      else -> null
    }
  }
