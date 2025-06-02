package osiris.core

import dev.langchain4j.model.chat.request.ResponseFormat
import dev.langchain4j.model.chat.request.ResponseFormatType
import dev.langchain4j.model.chat.request.json.JsonSchema
import kairo.reflect.KairoType
import kairo.serialization.typeReference
import osiris.schema.osirisSchema
import osiris.schema.osirisSchemaName

public abstract class OsirisResponseType<out Response : Any> {
  public abstract val format: ResponseFormat?

  public abstract fun convert(text: String): Response

  public object Text : OsirisResponseType<String>() {
    override val format: Nothing? = null

    override fun convert(text: String): String =
      text
  }

  public class Json<out Response : Any>(
    private val type: KairoType<Response>,
  ) : OsirisResponseType<Response>() {
    override val format: ResponseFormat =
      ResponseFormat.builder()
        .type(ResponseFormatType.JSON)
        .jsonSchema(
          JsonSchema.builder()
            .name(osirisSchemaName(type.kotlinClass))
            .rootElement(osirisSchema(type.kotlinClass))
            .build(),
        )
        .build()

    override fun convert(text: String): Response =
      osirisMapper.readValue(text, type.typeReference)
  }
}
