package osiris.schema

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import dev.langchain4j.model.chat.request.json.JsonAnyOfSchema
import dev.langchain4j.model.chat.request.json.JsonArraySchema
import dev.langchain4j.model.chat.request.json.JsonBooleanSchema
import dev.langchain4j.model.chat.request.json.JsonEnumSchema
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
import osiris.schema.LlmSchema.Description
import osiris.schema.LlmSchema.LlmSchemaException
import osiris.schema.LlmSchema.SchemaName

internal object LlmSchemaGenerator {
  fun generateName(kClass: KClass<*>): String =
    withErrorWrapper({ "Failed to generate LLM schema name for ${kClass.qualifiedName!!}. $it" }) {
      val annotation = kClass.findAnnotation<SchemaName>()
        ?: throw LlmSchemaException("Missing @${SchemaName::class.simpleName!!}.")
      return@withErrorWrapper annotation.schemaName
    }

  fun generateSchema(kClass: KClass<*>): JsonObjectSchema =
    withErrorWrapper({ "Failed to generate LLM schema for ${kClass.qualifiedName!!}. $it" }) {
      if (!kClass.isData) throw LlmSchemaException("Must be a data class or data object.")
      return@withErrorWrapper objectElement(null, kClass)
    }

  private fun element(element: KAnnotatedElement, type: KType): JsonSchemaElement {
    val kClass = type.classifier as KClass<*>
    val description = parseDescription(element)
    return when (parseLlmType(element, kClass)) {
      LlmType.Boolean -> booleanElement(description)
      LlmType.Enum -> enumElement(description, kClass)
      LlmType.Integer -> integerElement(description)
      LlmType.List -> listElement(description, checkNotNull(type.arguments.single().type))
      LlmType.Number -> numberElement(description)
      LlmType.Object -> objectElement(description, kClass)
      LlmType.Polymorphic -> polymorphicElement(description, kClass)
      LlmType.String -> stringElement(description)
    }
  }

  private fun booleanElement(description: String?): JsonBooleanSchema =
    JsonBooleanSchema.builder().apply {
      description(description)
    }.build()

  private fun enumElement(description: String?, kClass: KClass<*>): JsonEnumSchema =
    JsonEnumSchema.builder().apply {
      description(description)
      enumValues(kClass.java.enumConstants.map { (it as Enum<*>).name })
    }.build()

  private fun integerElement(description: String?): JsonIntegerSchema =
    JsonIntegerSchema.builder().apply {
      description(description)
    }.build()

  private fun listElement(description: String?, type: KType): JsonArraySchema =
    JsonArraySchema.builder().apply {
      description(description)
      val element = element(type, type)
      items(element)
    }.build()

  private fun numberElement(description: String?): JsonNumberSchema =
    JsonNumberSchema.builder().apply {
      description(description)
    }.build()

  private fun objectElement(description: String?, kClass: KClass<*>): JsonObjectSchema =
    JsonObjectSchema.builder().apply {
      description(description)
      val required: MutableList<String> = mutableListOf()
      getAllParams(kClass).forEach { param ->
        val name = checkNotNull(param.name)
        val element = withErrorWrapper({ message -> "${message.dropLastWhile { it == '.' }} for property $name." }) {
          element(param, param.type)
        }
        addProperty(name, element)
        if (param.isOptional) throw LlmSchemaException("${param.name!!} must not be optional.")
        if (!param.type.isMarkedNullable) {
          required += name
        }
      }
      required(required)
    }.build()

  private fun polymorphicElement(description: String?, kClass: KClass<*>): JsonAnyOfSchema =
    JsonAnyOfSchema.builder().apply {
      description(description)
      val jsonTypeInfo = checkNotNull(kClass.findAnnotation<JsonTypeInfo>())
      if (jsonTypeInfo.use != JsonTypeInfo.Id.NAME) {
        throw LlmSchemaException("@${JsonTypeInfo::class.simpleName!!} must be have use = NAME.")
      }
      if (jsonTypeInfo.include != JsonTypeInfo.As.PROPERTY) {
        throw LlmSchemaException("@${JsonTypeInfo::class.simpleName!!} must be have include = PROPERTY.")
      }
      if (jsonTypeInfo.defaultImpl != JsonTypeInfo::class) {
        throw LlmSchemaException("@${JsonTypeInfo::class.simpleName!!} must not specify defaultImpl.")
      }
      if (jsonTypeInfo.visible) {
        throw LlmSchemaException("@${JsonTypeInfo::class.simpleName!!} must not be visible.")
      }
      val jsonSubTypes = checkNotNull(kClass.findAnnotation<JsonSubTypes>())
      anyOf(
        jsonSubTypes.value.map { subType ->
          val delegate = objectElement(null, subType.value)
          return@map JsonObjectSchema.builder().apply {
            description(delegate.description())
            addEnumProperty(jsonTypeInfo.property, listOf(subType.name))
            addProperties(delegate.properties())
            required(
              buildList {
                add(jsonTypeInfo.property)
                addAll(delegate.required())
              },
            )
          }.build()
        },
      )
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
    } catch (e: LlmSchemaException) {
      throw LlmSchemaException(transformMessage(e.message), e)
    }
  }
}
