package osiris.schema

import dev.langchain4j.model.chat.request.json.JsonAnyOfSchema
import dev.langchain4j.model.chat.request.json.JsonBooleanSchema
import dev.langchain4j.model.chat.request.json.JsonEnumSchema
import dev.langchain4j.model.chat.request.json.JsonIntegerSchema
import dev.langchain4j.model.chat.request.json.JsonNullSchema
import dev.langchain4j.model.chat.request.json.JsonNumberSchema
import dev.langchain4j.model.chat.request.json.JsonObjectSchema
import dev.langchain4j.model.chat.request.json.JsonSchemaElement
import dev.langchain4j.model.chat.request.json.JsonStringSchema
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.elementDescriptors
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.serializer

public object SchemaGenerator {
  public inline fun <reified T : Any> generate(): JsonSchemaElement =
    generate(serializer<T>().descriptor)

  public fun generate(descriptor: SerialDescriptor, annotations: List<Annotation> = emptyList()): JsonSchemaElement =
    @Suppress("ElseCaseInsteadOfExhaustiveWhen")
    when (descriptor.kind) {
      is StructureKind.CLASS -> generateClass(descriptor, annotations)
      is StructureKind.OBJECT -> generateObject(descriptor, annotations)
      is PolymorphicKind.SEALED -> generatePolymorphic(descriptor, annotations)
      is SerialKind.ENUM -> generateEnum(descriptor, annotations)
      is PrimitiveKind.BOOLEAN -> generateBoolean(descriptor, annotations)
      is PrimitiveKind.INT -> generateInteger(descriptor, annotations)
      is PrimitiveKind.LONG -> generateInteger(descriptor, annotations)
      is PrimitiveKind.FLOAT -> generateNumber(descriptor, annotations)
      is PrimitiveKind.DOUBLE -> generateNumber(descriptor, annotations)
      is PrimitiveKind.STRING -> generateString(descriptor, annotations)
      else -> error("Unsupported kind (kind=${descriptor.kind}).")
    }
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
    val schema = jsonObjectSchema(
      description = annotation<Schema.Description>(allAnnotations)?.value,
      properties = properties,
    )
    return if (descriptor.isNullable) schema.orNull() else schema
  }

  private fun generateObject(descriptor: SerialDescriptor, annotations: List<Annotation>): JsonSchemaElement {
    val allAnnotations = descriptor.annotations + annotations
    val schema = jsonObjectSchema(
      description = annotation<Schema.Description>(allAnnotations)?.value,
      properties = emptyMap(),
    )
    return if (descriptor.isNullable) schema.orNull() else schema
  }

  private fun generatePolymorphic(descriptor: SerialDescriptor, annotations: List<Annotation>): JsonSchemaElement {
    check(descriptor.elementsCount == 2) {
      "Polymorphic descriptor must have exactly 2 elements (descriptor=$descriptor)."
    }
    val allAnnotations = descriptor.annotations + annotations
    val optionsDescriptor = descriptor.getElementDescriptor(1)
    val schema = JsonAnyOfSchema.builder().apply {
      annotation<Schema.Description>(allAnnotations)?.let { description(it.value) }
      anyOf(
        buildList {
          optionsDescriptor.elementDescriptors.forEach { optionDescriptor ->
            add(
              jsonObjectSchema(
                description = annotation<Schema.Description>(optionDescriptor.annotations)?.value,
                properties = buildMap {
                  put(
                    key = descriptor.getElementName(0),
                    value = JsonEnumSchema.builder().apply {
                      enumValues(optionDescriptor.serialName)
                    }.build(),
                  )
                  repeat(optionDescriptor.elementsCount) { i ->
                    put(
                      key = optionDescriptor.getElementName(i),
                      value = generate(
                        descriptor = optionDescriptor.getElementDescriptor(i),
                        annotations = optionDescriptor.getElementAnnotations(i),
                      ),
                    )
                  }
                },
              ),
            )
          }
        },
      )
    }.build()
    return if (descriptor.isNullable) schema.orNull() else schema
  }

  private fun jsonObjectSchema(
    description: String?,
    properties: Map<String, JsonSchemaElement>,
  ): JsonObjectSchema =
    JsonObjectSchema.builder().apply {
      description?.let { description(it) }
      addProperties(properties)
      required(properties.keys.toList())
    }.build()

  private fun generateEnum(descriptor: SerialDescriptor, annotations: List<Annotation>): JsonSchemaElement {
    val schema = JsonEnumSchema.builder().apply {
      annotation<Schema.Description>(annotations)?.let { description(it.value) }
      enumValues(descriptor.elementNames.toList())
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
