package osiris.core

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import kairo.serialization.util.readValueSpecial

public inline fun <reified Response : Any> List<ChatMessage>.getResponseAs(): Response? =
  getResponse()?.let { osirisMapper.readValueSpecial<Response>(it) }

public fun List<ChatMessage>.getResponse(): String? =
  (last() as AiMessage).text()
