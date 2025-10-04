package osiris.schema

import dev.langchain4j.model.chat.request.json.JsonAnyOfSchema
import dev.langchain4j.model.chat.request.json.JsonBooleanSchema
import dev.langchain4j.model.chat.request.json.JsonIntegerSchema
import dev.langchain4j.model.chat.request.json.JsonNullSchema
import dev.langchain4j.model.chat.request.json.JsonNumberSchema
import dev.langchain4j.model.chat.request.json.JsonObjectSchema
import dev.langchain4j.model.chat.request.json.JsonSchemaElement
import dev.langchain4j.model.chat.request.json.JsonStringSchema
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.serializer

public object SchemaGenerator {
  public inline fun <reified T : Any> generate(): JsonSchemaElement =
    generate(serializer<T>().descriptor)

  public fun generate(descriptor: SerialDescriptor, annotations: List<Annotation> = emptyList()): JsonSchemaElement =
    @Suppress("ElseCaseInsteadOfExhaustiveWhen")
    when (descriptor.kind) {
      is StructureKind.CLASS -> generateClass(descriptor, annotations)
      is StructureKind.OBJECT -> generateObject(descriptor, annotations)
      is PrimitiveKind.BOOLEAN -> generateBoolean(descriptor, annotations)
      is PrimitiveKind.INT -> generateInteger(descriptor, annotations)
      is PrimitiveKind.LONG -> generateInteger(descriptor, annotations)
      is PrimitiveKind.FLOAT -> generateNumber(descriptor, annotations)
      is PrimitiveKind.DOUBLE -> generateNumber(descriptor, annotations)
      is PrimitiveKind.STRING -> generateString(descriptor, annotations)
      else -> error("Unsupported kind (kind=${descriptor.kind}).")
    }
  // TODO: Sealed class
  // TODO: Enum
  // TODO: List

  private fun generateClass(descriptor: SerialDescriptor, annotations: List<Annotation>): JsonSchemaElement {
    val allAnnotations = descriptor.annotations + annotations
    val properties = buildMap {
      repeat(descriptor.elementsCount) { i ->
        put(
          key = descriptor.getElementName(i),
          value = generate(
            descriptor = descriptor.getElementDescriptor(i),
            annotations = descriptor.getElementAnnotations(i),
          ),
        )
      }
    }
    val schema = JsonObjectSchema.builder().apply {
      annotation<Schema.Description>(allAnnotations)?.let { description(it.value) }
      addProperties(properties)
      required(properties.keys.toList())
    }.build()
    return if (descriptor.isNullable) schema.orNull() else schema
  }

  private fun generateObject(descriptor: SerialDescriptor, annotations: List<Annotation>): JsonSchemaElement {
    val allAnnotations = descriptor.annotations + annotations
    val schema = JsonObjectSchema.builder().apply {
      annotation<Schema.Description>(allAnnotations)?.let { description(it.value) }
      required()
    }.build()
    return if (descriptor.isNullable) schema.orNull() else schema
  }

  private fun generateBoolean(descriptor: SerialDescriptor, annotations: List<Annotation>): JsonSchemaElement {
    val schema = JsonBooleanSchema.builder().apply {
      annotation<Schema.Description>(annotations)?.let { description(it.value) }
    }.build()
    return if (descriptor.isNullable) schema.orNull() else schema
  }

  private fun generateInteger(descriptor: SerialDescriptor, annotations: List<Annotation>): JsonSchemaElement {
    val schema = JsonIntegerSchema.builder().apply {
      annotation<Schema.Description>(annotations)?.let { description(it.value) }
    }.build()
    return if (descriptor.isNullable) schema.orNull() else schema
  }

  private fun generateNumber(descriptor: SerialDescriptor, annotations: List<Annotation>): JsonSchemaElement {
    val schema = JsonNumberSchema.builder().apply {
      annotation<Schema.Description>(annotations)?.let { description(it.value) }
    }.build()
    return if (descriptor.isNullable) schema.orNull() else schema
  }

  private fun generateString(descriptor: SerialDescriptor, annotations: List<Annotation>): JsonSchemaElement {
    val schema = JsonStringSchema.builder().apply {
      annotation<Schema.Description>(annotations)?.let { description(it.value) }
    }.build()
    return if (descriptor.isNullable) schema.orNull() else schema
  }

  private inline fun <reified T : Annotation> annotation(annotations: List<Annotation>): T? =
    annotations.filterIsInstance<T>().lastOrNull()

  private fun JsonSchemaElement.orNull(): JsonSchemaElement =
    JsonAnyOfSchema.builder().apply {
      anyOf(this@orNull, JsonNullSchema())
    }.build()
}
