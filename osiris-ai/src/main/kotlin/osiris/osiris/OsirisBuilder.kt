package osiris.osiris

import osiris.osiris.responseConverter.OsirisResponseType
import osiris.osiris.responseConverter.StringResponseType

public class OsirisBuilder<Response : Any>(
  private val model: OsirisModel,
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
