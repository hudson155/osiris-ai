package osiris.core

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import kairo.serialization.util.readValueSpecial

/**
 * Helper to convert responses to the appropriate type.
 *
 * This assumes the last message is an [AiMessage], which is true with [ExitCondition.Default].
 * If you customize the exit condition, this helper might not work.
 */
public inline fun <reified Response : Any> List<ChatMessage>.convert(): Response {
  val lastMessage = last() as AiMessage
  val response = llmMapper.readValueSpecial<Response>(lastMessage.text())
  return checkNotNull(response)
}
