package osiris.schema

import dev.langchain4j.model.chat.request.json.JsonAnyOfSchema
import dev.langchain4j.model.chat.request.json.JsonArraySchema
import dev.langchain4j.model.chat.request.json.JsonBooleanSchema
import dev.langchain4j.model.chat.request.json.JsonEnumSchema
import dev.langchain4j.model.chat.request.json.JsonIntegerSchema
import dev.langchain4j.model.chat.request.json.JsonNullSchema
import dev.langchain4j.model.chat.request.json.JsonNumberSchema
import dev.langchain4j.model.chat.request.json.JsonObjectSchema
import dev.langchain4j.model.chat.request.json.JsonSchemaElement
import dev.langchain4j.model.chat.request.json.JsonStringSchema
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.valueParameters

internal data class StructuredBuilder(
  private val path: String?,
  private val type: KType,
) {
  private enum class Kind {
    Array,
    Boolean,
    Enum,
    Integer,
    Number,
    Object,
    Polymorphic,
    String,
  }

  fun generate(): JsonSchemaElement {
    getExplicitKind()?.let { return kind(it) }
    getWellKnownKind()?.let { return kind(it) }
    getEnumKind()?.let { return kind(it) }
    getArrayKind()?.let { return kind(it) }
    getKotlinKind()?.let { return kind(it) }
    throw IllegalArgumentException("Unable to determine type for $type.")
  }

  private fun getExplicitKind(): Kind? {
    val typeAnnotations = type.findAnnotations<Structured.Type>()
    if (typeAnnotations.isEmpty()) return null
    return when (typeAnnotations.single().value) {
      StructureType.Boolean -> Kind.Boolean
      StructureType.Integer -> Kind.Integer
      StructureType.Number -> Kind.Number
      StructureType.String -> Kind.String
    }
  }

  /**
   * Type support here is based on kairo-serialization's type support.
   */
  @Suppress("LongMethod")
  private fun getWellKnownKind(): Kind? =
    when (type.classifier) {
      CharArray::class -> Kind.String
      Char::class -> Kind.String
      String::class -> Kind.String
      BigDecimal::class -> Kind.Number
      BigInteger::class -> Kind.Integer
      Byte::class -> Kind.Integer
      UByte::class -> Kind.Integer
      Double::class -> Kind.Number
      Float::class -> Kind.Number
      Int::class -> Kind.Integer
      UInt::class -> Kind.Integer
      Long::class -> Kind.Integer
      ULong::class -> Kind.Integer
      Short::class -> Kind.Integer
      UShort::class -> Kind.Integer
      java.time.DayOfWeek::class -> Kind.String
      kotlinx.datetime.DayOfWeek::class -> Kind.String
      java.time.Duration::class -> Kind.String
      kotlin.time.Duration::class -> Kind.String
      java.time.Instant::class -> Kind.String
      kotlin.time.Instant::class -> Kind.String
      java.time.LocalDateTime::class -> Kind.String
      kotlinx.datetime.LocalDateTime::class -> Kind.String
      java.time.LocalDate::class -> Kind.String
      kotlinx.datetime.LocalDate::class -> Kind.String
      java.time.LocalTime::class -> Kind.String
      kotlinx.datetime.LocalTime::class -> Kind.String
      java.time.MonthDay::class -> Kind.String
      java.time.Month::class -> Kind.String
      kotlinx.datetime.Month::class -> Kind.String
      java.time.OffsetDateTime::class -> Kind.String
      java.time.OffsetTime::class -> Kind.String
      java.time.Period::class -> Kind.String
      kotlinx.datetime.DatePeriod::class -> Kind.String
      java.time.YearMonth::class -> Kind.String
      kotlinx.datetime.YearMonth::class -> Kind.String
      java.time.Year::class -> Kind.String
      java.time.ZonedDateTime::class -> Kind.String
      java.time.ZoneId::class -> Kind.String
      kotlinx.datetime.TimeZone::class -> Kind.String
      java.time.ZoneOffset::class -> Kind.String
      kotlinx.datetime.FixedOffsetTimeZone::class -> Kind.String
      Boolean::class -> Kind.Boolean
      java.util.UUID::class -> Kind.String
      kotlin.uuid.Uuid::class -> Kind.String
      else -> null
    }

  private fun getEnumKind(): Kind? {
    val kClass = type.classifier as? KClass<*> ?: return null
    if (kClass.java.isEnum) return Kind.Enum
    return null
  }

  private fun getArrayKind(): Kind? =
    when (type.classifier) {
      List::class -> Kind.Array
      Set::class -> Kind.Array
      else -> null
    }

  private fun getKotlinKind(): Kind? {
    val kClass = type.classifier as? KClass<*> ?: return null
    if (kClass.isSealed) return Kind.Polymorphic
    if (kClass.isData) return Kind.Object
    return null
  }

  private fun kind(kind: Kind): JsonSchemaElement =
    when (kind) {
      Kind.Array -> kindArray()
      Kind.Boolean -> kindBoolean()
      Kind.Enum -> kindEnum()
      Kind.Integer -> kindInteger()
      Kind.Number -> kindNumber()
      Kind.Object -> kindObject()
      Kind.Polymorphic -> kindPolymorphic()
      Kind.String -> kindString()
    }

  private fun kindArray(): JsonSchemaElement {
    val description = getDescription()
    val schema = JsonArraySchema.builder().apply {
      description.takeIf { !type.isMarkedNullable }?.let { description(it) }
      val builder = copy(path = "$path[]", type = checkNotNull(type.arguments.single().type))
      val schema = builder.generate()
      items(schema)
    }.build()
    if (type.isMarkedNullable) return schema.nullable()
    return schema
  }

  private fun kindBoolean(): JsonSchemaElement {
    val description = getDescription()
    val schema = JsonBooleanSchema.builder().apply {
      description.takeIf { !type.isMarkedNullable }?.let { description(it) }
    }.build()
    if (type.isMarkedNullable) return schema.nullable()
    return schema
  }

  private fun kindEnum(): JsonSchemaElement {
    val description = getDescription()
    val schema = JsonEnumSchema.builder().apply {
      description.takeIf { !type.isMarkedNullable }?.let { description(it) }
      val kClass = type.classifier as KClass<*>
      enumValues(kClass.java.enumConstants.map { (it as Enum<*>).name })
    }.build()
    if (type.isMarkedNullable) return schema.nullable()
    return schema
  }

  private fun kindInteger(): JsonSchemaElement {
    val description = getDescription()
    val schema = JsonIntegerSchema.builder().apply {
      description.takeIf { !type.isMarkedNullable }?.let { description(it) }
    }.build()
    if (type.isMarkedNullable) return schema.nullable()
    return schema
  }

  private fun kindNumber(): JsonSchemaElement {
    val description = getDescription()
    val schema = JsonNumberSchema.builder().apply {
      description.takeIf { !type.isMarkedNullable }?.let { description(it) }
    }.build()
    if (type.isMarkedNullable) return schema.nullable()
    return schema
  }

  private fun kindObject(): JsonSchemaElement {
    val description = getDescription()
    val schema = JsonObjectSchema.builder().apply {
      description.takeIf { !type.isMarkedNullable }?.let { description(it) }
      val kClass = type.classifier as KClass<*>
      val params = run {
        if (kClass.objectInstance != null) return@run emptyList()
        return@run checkNotNull(kClass.primaryConstructor).valueParameters
      }
      params.forEach { param ->
        val paramName = checkNotNull(param.name)
        val builder = copy(path = "$path.$paramName", type = param.type)
        val schema = builder.generate()
        addProperty(paramName, schema)
      }
      required(params.filterNot { it.isOptional }.map { checkNotNull(it.name) })
      additionalProperties(false)
    }.build()
    if (type.isMarkedNullable) return schema.nullable()
    return schema
  }

  private fun kindPolymorphic(): JsonSchemaElement {
    val description = getDescription()
    return JsonAnyOfSchema.builder().apply {
      description?.let { description(it) }
      val kClass = type.classifier as KClass<*>
      anyOf(
        buildList {
          kClass.sealedSubclasses.forEach { subclass ->
            val discriminatorAnnotations = subclass.findAnnotations<Structured.Discriminator>()
            require(discriminatorAnnotations.isNotEmpty())
            val discriminator = discriminatorAnnotations.single().value
            val builder = copy(path = "$path[$discriminator]", type = subclass.createType())
            val schema = builder.generate() as JsonObjectSchema
            add(
              JsonObjectSchema.builder().apply {
                schema.description()?.let { description(it) }
                addEnumProperty("type", listOf(discriminator))
                schema.properties().forEach { (name, schema) -> addProperty(name, schema) }
                required(listOf("type") + schema.required())
                additionalProperties(schema.additionalProperties())
              }.build(),
            )
          }
          if (type.isMarkedNullable) add(JsonNullSchema)
        },
      )
    }.build()
  }

  private fun kindString(): JsonSchemaElement {
    val description = getDescription()
    val schema = JsonStringSchema.builder().apply {
      description.takeIf { !type.isMarkedNullable }?.let { description(it) }
    }.build()
    if (type.isMarkedNullable) return schema.nullable()
    return schema
  }

  private fun JsonSchemaElement.nullable(): JsonSchemaElement {
    val description = getDescription()
    return JsonAnyOfSchema.builder().apply {
      description?.let { description(it) }
      anyOf(listOf(this@nullable, JsonNullSchema))
    }.build()
  }

  private fun getDescription(): String? {
    val classifierAnnotations = (type.classifier as KAnnotatedElement).findAnnotations<Structured.Description>()
    if (classifierAnnotations.isNotEmpty()) return classifierAnnotations.single().value
    val typeAnnotations = type.findAnnotations<Structured.Description>()
    if (typeAnnotations.isNotEmpty()) return typeAnnotations.single().value
    return null
  }
}
