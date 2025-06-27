package osiris.schema

import dev.langchain4j.model.chat.request.json.JsonObjectSchema
import kotlin.reflect.KClass

/**
 * Osiris supports automatic LLM (OpenAPI) schema generation for structured output and tool calls,
 * so you don't need to manage schemas yourself.
 */
public object LlmSchema {
  /**
   * Some providers (such as OpenAI) require that structured output schemas specify a name.
   * This is only necessary for structured output, not for tool calls.
   */
  @Target(AnnotationTarget.CLASS)
  public annotation class SchemaName(val schemaName: String)

  /**
   * Support custom types or override the type for a supported primitive.
   * The value can be `boolean`, `integer`, `string`, or `number`.
   */
  @Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
  public annotation class Type(val type: String)

  @Target(AnnotationTarget.CLASS)
  public annotation class Polymorphic(val discriminator: String, val subTypes: Array<Subtype>) {
    public annotation class Subtype(val value: KClass<*>, val name: String)
  }

  /**
   * Descriptions are available to the LLM, helping it understand your schema better.
   */
  @Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
  public annotation class Description(val description: String)

  public class LlmSchemaException internal constructor(
    override val message: String,
    override val cause: Throwable? = null,
  ) : IllegalArgumentException(message)

  public fun generateName(kClass: KClass<*>): String =
    LlmSchemaGenerator.generateName(kClass)

  public fun generate(kClass: KClass<*>): JsonObjectSchema =
    LlmSchemaGenerator.generateSchema(kClass)
}
