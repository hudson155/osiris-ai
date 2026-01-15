package osiris.schema

import dev.langchain4j.model.chat.request.json.JsonSchemaElement
import kotlin.reflect.KType
import kotlin.reflect.typeOf

public object Structured {
  @Target(AnnotationTarget.CLASS)
  public annotation class Discriminator(val value: String)

  @Target(AnnotationTarget.VALUE_PARAMETER)
  public annotation class Type(val value: StructureType)

  @Target(AnnotationTarget.CLASS, AnnotationTarget.VALUE_PARAMETER)
  public annotation class Description(val value: String)

  public inline fun <reified T> generate(): JsonSchemaElement =
    generate(typeOf<T>())

  public fun generate(type: KType): JsonSchemaElement =
    StructuredBuilder(path = null, type = type).generate()
}
