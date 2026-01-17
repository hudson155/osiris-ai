package osiris.schema

import dev.langchain4j.model.chat.request.json.JsonSchema
import dev.langchain4j.model.chat.request.json.JsonSchemaElement
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.typeOf

public object Structured {
  /**
   * The name of the schema.
   * Some LLM providers use this for caching.
   */
  @Target(AnnotationTarget.CLASS)
  public annotation class Name(val value: String)

  /**
   * The discriminator is needed when generating schemas that include sealed (polymorphic) classes.
   */
  @Target(AnnotationTarget.CLASS)
  public annotation class Discriminator(val value: String)

  /**
   * Use this to override the default [StructureType].
   */
  @Target(AnnotationTarget.VALUE_PARAMETER)
  public annotation class Type(val value: StructureType)

  /**
   * Use this to add an optional description.
   */
  @Target(AnnotationTarget.CLASS, AnnotationTarget.VALUE_PARAMETER)
  public annotation class Description(val value: String)

  public inline fun <reified T> schema(name: String? = null): JsonSchema =
    schema(typeOf<T>(), name)

  public fun schema(type: KType, name: String? = null): JsonSchema {
    val element = element(type)
    return JsonSchema.builder().apply {
      name(getName(type, name))
      rootElement(element)
    }.build()
  }

  public inline fun <reified T> element(): JsonSchemaElement =
    element(typeOf<T>())

  public fun element(type: KType): JsonSchemaElement =
    StructuredBuilder(null, type).generate()

  private fun getName(type: KType, name: String?): String {
    val kClass = (type.classifier as KClass<*>)
    val annotations = kClass.findAnnotations<Name>()
    if (annotations.isNotEmpty()) return annotations.single().value
    return requireNotNull(name) {
      "${error.structuredOutput(kClass)}: Must define ${error.nameAnnotation}."
    }
  }
}
