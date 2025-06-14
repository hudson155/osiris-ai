package osiris.core

import dev.langchain4j.data.message.AiMessage
import kairo.serialization.util.readValueSpecial
import osiris.core2.llmMapper

/**
 * Helper to convert responses to the appropriate type.
 */
public inline fun <reified Response : Any> AiMessage.convert(): Response =
  checkNotNull(llmMapper.readValueSpecial<Response>(text()))
