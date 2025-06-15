package osiris.core

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.UserMessage

internal fun deriveText(messages: List<ChatMessage>): String =
  messages.asReversed().firstNotNullOf { deriveText(it) }

internal fun deriveText(message: ChatMessage): String? =
  when (message) {
    is AiMessage -> deriveText(message)
    is UserMessage -> deriveText(message)
    else -> null
  }

internal fun deriveText(message: AiMessage): String =
  message.text()

internal fun deriveText(message: UserMessage): String =
  message.singleText()
