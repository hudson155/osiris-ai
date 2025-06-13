package osiris.core

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import kairo.serialization.util.readValueSpecial

public inline fun <reified Response : Any> ChatMessage.convert(): Response =
  checkNotNull(llmMapper.readValueSpecial<Response>((this as AiMessage).text()))
