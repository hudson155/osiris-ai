package osiris.core

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import kairo.serialization.util.readValueSpecial

/**
 * Helper to convert responses to the appropriate type.
 */
public inline fun <reified Response : Any> List<ChatMessage>.convert(): Response {
  val lastMessage = last() as AiMessage
  return checkNotNull(llmMapper.readValueSpecial<Response>(lastMessage.text()))
}
