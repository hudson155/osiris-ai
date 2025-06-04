package osiris.core

import dev.langchain4j.data.message.AiMessage
import kairo.serialization.util.readValueSpecial

public inline fun <reified Response : Any> AiMessage.convert(): Response? =
  text()?.let { osirisMapper.readValueSpecial<Response>(it) }
