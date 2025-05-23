package osiris.core.responseConverter

import dev.langchain4j.model.chat.request.ResponseFormat
import dev.langchain4j.model.chat.request.ResponseFormatType
import dev.langchain4j.model.chat.request.json.JsonObjectSchema
import dev.langchain4j.model.chat.request.json.JsonSchema
import dev.langchain4j.model.chat.response.ChatResponse
import kairo.reflect.KairoType
import kairo.serialization.typeReference
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.valueParameters
import osiris.core.osirisMapper
import osiris.core.schema.OsirisSchema

public abstract class JsonResponseType<Response : Any> : OsirisResponseType<Response>() {
  private val type: KairoType<Response> = KairoType.from(JsonResponseType::class, 0, this::class)

  public fun format(): ResponseFormat {
    val kClass = type.kotlinClass
    require(kClass.isData) {
      "Osiris schema ${kClass.qualifiedName!!}" +
        " must be a data class or data object."
    }
    val rootElement = JsonObjectSchema.builder().apply {
      getAllParams(kClass).forEach { param ->
        val name = checkNotNull(param.name)
        val type = parseType(kClass, param)
        val description = parseDescription(param)
        when (type) {
          "boolean" -> addBooleanProperty(name, description)
          "integer" -> addIntegerProperty(name, description)
          "number" -> addNumberProperty(name, description)
          "string" -> addStringProperty(name, description)
          else -> throw IllegalArgumentException("Unsupported type: $type.")
        }
      }
    }.build()
    val jsonSchema = JsonSchema.builder()
      .name(parseName(kClass))
      .rootElement(rootElement)
      .build()
    return ResponseFormat.builder()
      .type(ResponseFormatType.JSON)
      .jsonSchema(jsonSchema)
      .build()
  }

  private fun parseName(kType: KClass<*>): String {
    val annotation = requireNotNull(kType.findAnnotation<OsirisSchema.Name>()) {
      "Osiris schema ${kType.qualifiedName!!}" +
        " is missing @${OsirisSchema.Name::class.simpleName!!}."
    }
    return annotation.name
  }

  private fun parseType(kClass: KClass<Response>, param: KParameter): String {
    val annotation = requireNotNull(param.findAnnotation<OsirisSchema.Type>()) {
      "Osiris schema for ${kClass.qualifiedName!!}::${param.name!!}" +
        " is missing @${OsirisSchema.Type::class.simpleName!!}."
    }
    return annotation.type
  }

  private fun parseDescription(param: KParameter): String? {
    val annotation = param.findAnnotation<OsirisSchema.Description>()
    return annotation?.description
  }

  private fun getAllParams(kClass: KClass<Response>): List<KParameter> {
    if (kClass.objectInstance != null) return emptyList()
    val constructor = checkNotNull(kClass.primaryConstructor) {
      "Data classes always have primary constructors."
    }
    return constructor.valueParameters
  }

  @Suppress("ForbiddenMethodCall")
  override fun convert(langchainResponse: ChatResponse): Response =
    osirisMapper.readValue(langchainResponse.aiMessage().text(), type.typeReference)
}
