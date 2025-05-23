package osiris.core

import dev.langchain4j.model.chat.ChatModel
import osiris.core.responseConverter.OsirisResponseType
import osiris.core.responseConverter.StringResponseType

public class OsirisBuilder<Response : Any>(
  private val model: ChatModel,
) {
  public var responseType: OsirisResponseType<Response>? = null

  public fun build(): Osiris<Response> {
    val responseConverter = responseType ?: defaultResponseType()
    return Osiris(
      model = model,
      responseType = responseConverter,
    )
  }

  private fun defaultResponseType(): OsirisResponseType<Response> {
    try {
      @Suppress("UNCHECKED_CAST")
      return StringResponseType as OsirisResponseType<Response>
    } catch (e: ClassCastException) {
      throw IllegalArgumentException("Osiris response converter must be set for non-string response types.", e)
    }
  }
}
