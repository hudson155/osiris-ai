package osiris.chat

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import kairo.serialization.util.kairoReadSpecial
import osiris.core.llmMapper

/**
 * Helper to convert responses to the appropriate type.
 *
 * This assumes the last message is an [AiMessage], which is true with [ExitCondition.Default].
 * If you customize the exit condition, this helper might not work.
 */
public inline fun <reified Response : Any> List<ChatMessage>.convert(): Response {
  val lastMessage = last() as AiMessage
  val response = llmMapper.kairoReadSpecial<Response>(lastMessage.text())
  return checkNotNull(response)
}
