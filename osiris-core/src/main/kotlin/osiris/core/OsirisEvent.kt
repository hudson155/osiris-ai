package osiris.core

import dev.langchain4j.data.message.ChatMessage

public sealed class OsirisEvent<out Response : Any> {
  public data class Message(val message: ChatMessage) : OsirisEvent<Nothing>()

  public data class Response<out Response : Any>(val response: Response?) : OsirisEvent<Response>()
}
