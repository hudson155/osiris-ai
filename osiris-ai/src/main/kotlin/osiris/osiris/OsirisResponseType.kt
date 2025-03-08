package osiris.osiris

import com.fasterxml.jackson.databind.json.JsonMapper
import com.openai.core.JsonArray
import com.openai.core.JsonBoolean
import com.openai.core.JsonObject
import com.openai.core.JsonString
import com.openai.models.ChatCompletionCreateParams.ResponseFormat
import com.openai.models.ResponseFormatJsonSchema
import com.openai.models.ResponseFormatJsonSchema.JsonSchema
import com.openai.models.ResponseFormatText
import kairo.reflect.KairoType
import kairo.serialization.jsonMapper
import kairo.serialization.typeReference
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.valueParameters

public abstract class OsirisResponseType<out Response : Any> {
  internal abstract fun responseFormat(): ResponseFormat

  internal abstract fun convert(string: String): Response

  public class Text : OsirisResponseType<String>() {
    override fun responseFormat(): ResponseFormat =
      ResponseFormat.ofText(ResponseFormatText.builder().build())

    override fun convert(string: String): String =
      string
  }

  public class Json<out Response : Any>(
    private val type: KairoType<Response>,
  ) : OsirisResponseType<Response>() {
    private val mapper: JsonMapper = jsonMapper().build()

    override fun responseFormat(): ResponseFormat {
      val kClass = type.kotlinClass
      require(kClass.isData) {
        "Osiris schema ${kClass.qualifiedName!!}" +
          " must be a data class or data object."
      }
      val params = getAllParams(kClass)
      return ResponseFormat.ofJsonSchema(
        ResponseFormatJsonSchema.builder().apply {
          jsonSchema(
            JsonSchema.builder().apply {
              name(kClass.findAnnotation<OsirisSchema.Name>()!!.name)
              schema(
                JsonSchema.Schema.builder().apply {
                  putAdditionalProperty("type", JsonString.of("object"))
                  putAdditionalProperty("properties", JsonArray.of(params.map { param ->
                    val name = checkNotNull(param.name)
                    val type = requireNotNull(param.findAnnotation<OsirisSchema.Type>()) {
                      "Osiris schema for ${kClass.qualifiedName!!}" +
                        " is missing @${OsirisSchema.Type::class.simpleName!!}."
                    }.type
                    val description = param.findAnnotation<OsirisSchema.Description>()?.description
                    return@map JsonObject.of(mapOf(
                      name to JsonObject.of(buildMap {
                        put("type", JsonString.of(type))
                        description?.let { put("description", JsonString.of(it)) }
                      }),
                    ))
                  }))
                  putAdditionalProperty("required", JsonArray.of(listOf(
                    JsonString.of("fullName"),
                    JsonString.of("age"),
                    // todo dynamic
                  )))
                  putAdditionalProperty("additionalProperties", JsonBoolean.of(false))
                }.build()
              )
              strict(true)
            }.build()
          )
        }.build()
      )
    }

    private fun getAllParams(
      endpointKClass: KClass<Response>,
    ): List<KParameter> {
      if (endpointKClass.objectInstance != null) return emptyList()
      val constructor = checkNotNull(endpointKClass.primaryConstructor) {
        "Data classes always have primary constructors."
      }
      return constructor.valueParameters
    }

    override fun convert(string: String): Response =
      mapper.readerFor(type.typeReference).readValue(string)
  }
}
