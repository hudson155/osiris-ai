package osiris.schema

import dev.langchain4j.model.chat.request.json.JsonArraySchema
import dev.langchain4j.model.chat.request.json.JsonBooleanSchema
import dev.langchain4j.model.chat.request.json.JsonIntegerSchema
import dev.langchain4j.model.chat.request.json.JsonNumberSchema
import dev.langchain4j.model.chat.request.json.JsonObjectSchema
import dev.langchain4j.model.chat.request.json.JsonSchemaElement
import dev.langchain4j.model.chat.request.json.JsonStringSchema
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.valueParameters

public object LlmSchema {
  @Target(AnnotationTarget.CLASS)
  public annotation class SchemaName(val schemaName: String)

  @Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
  public annotation class Type(val type: String)

  @Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
  public annotation class Description(val description: String)

  internal class Exception(
    override val message: String,
    override val cause: Throwable? = null,
  ) : IllegalArgumentException(message)

  public fun generateName(kClass: KClass<*>): String =
    withErrorWrapper({ "Failed to generate LLM schema name for ${kClass.qualifiedName!!}. $it" }) {
      val annotation = kClass.findAnnotation<SchemaName>()
        ?: throw Exception("Missing @${SchemaName::class.simpleName!!}.")
      return@withErrorWrapper annotation.schemaName
    }

  public fun generate(kClass: KClass<*>): JsonObjectSchema =
    withErrorWrapper({ "Failed to generate LLM schema for ${kClass.qualifiedName!!}. $it" }) {
      if (!kClass.isData) throw Exception("Must be a data class or data object.")
      return@withErrorWrapper objectElement(null, kClass)
    }

  private fun element(element: KAnnotatedElement, type: KType): JsonSchemaElement {
    val description = parseDescription(element)
    return when (parseType(element, type)) {
      LlmType.Boolean -> booleanElement(description)
      LlmType.Integer -> integerElement(description)
      LlmType.List -> listElement(description, type)
      LlmType.Number -> numberElement(description)
      LlmType.Object -> objectElement(description, type.classifier as KClass<*>)
      LlmType.String -> stringElement(description)
    }
  }

  private fun booleanElement(description: String?): JsonBooleanSchema =
    JsonBooleanSchema.builder().apply {
      description(description)
    }.build()

  private fun integerElement(description: String?): JsonIntegerSchema =
    JsonIntegerSchema.builder().apply {
      description(description)
    }.build()

  private fun listElement(
    description: String?,
    type: KType,
  ): JsonArraySchema =
    JsonArraySchema.builder().apply {
      description(description)
      val itemType = checkNotNull(type.arguments.single().type)
      val element = element(itemType, itemType)
      items(element)
    }.build()

  private fun numberElement(description: String?): JsonNumberSchema =
    JsonNumberSchema.builder().apply {
      description(description)
    }.build()

  private fun objectElement(
    description: String?,
    kClass: KClass<*>,
  ): JsonObjectSchema =
    JsonObjectSchema.builder().apply {
      description(description)
      val required: MutableList<String> = mutableListOf()
      getAllParams(kClass).forEach { param ->
        val name = checkNotNull(param.name)
        val element = withErrorWrapper({ message -> "${message.dropLastWhile { it == '.' }} for property $name." }) {
          element(param, param.type)
        }
        addProperty(name, element)
        if (param.isOptional) throw Exception("${param.name!!} must not be optional.")
        if (!param.type.isMarkedNullable) {
          required += name
        }
      }
      required(required)
    }.build()

  private fun stringElement(description: String?): JsonStringSchema =
    JsonStringSchema.builder().apply {
      description(description)
    }.build()

  private fun parseDescription(element: KAnnotatedElement): String? {
    val annotation = element.findAnnotation<Description>() ?: return null
    return annotation.description
  }

  private fun getAllParams(kClass: KClass<*>): List<KParameter> {
    if (kClass.objectInstance != null) return emptyList()
    val constructor = checkNotNull(kClass.primaryConstructor) {
      "Data classes always have primary constructors."
    }
    return constructor.valueParameters
  }

  private fun <T> withErrorWrapper(transformMessage: (message: String) -> String, block: () -> T): T {
    try {
      return block()
    } catch (e: Exception) {
      throw Exception(transformMessage(e.message))
    }
  }
}
