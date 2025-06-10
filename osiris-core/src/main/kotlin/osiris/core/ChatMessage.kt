package osiris.core

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import kairo.serialization.util.readValueSpecial

public inline fun <reified Response : Any> List<ChatMessage>.response(): Response {
  val message = asReversed().filterIsInstance<AiMessage>().first { !it.hasToolExecutionRequests() }
  return checkNotNull(llmMapper.readValueSpecial<Response>(message.text()))
}
