package osiris.osiris

import osiris.osiris.responseConverter.ResponseConverter
import osiris.osiris.responseConverter.StringResponseConverter

public class OsirisBuilder<Response : Any>(
  private val model: OsirisModel,
) {
  public var responseConverter: ResponseConverter<Response>? = null

  public fun build(): Osiris<Response> {
    val responseConverter = responseConverter ?: defaultResponseConverter()
    return Osiris(
      model = model,
      responseConverter = responseConverter,
    )
  }

  private fun defaultResponseConverter(): ResponseConverter<Response> {
    try {
      @Suppress("UNCHECKED_CAST")
      return StringResponseConverter as ResponseConverter<Response>
    } catch (e: ClassCastException) {
      // TODO: Can/should we automatically use JSON instead?
      throw IllegalArgumentException("Osiris response converter must be set for non-string response types.", e)
    }
  }
}
