package osiris.schema

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.math.BigDecimal
import java.math.BigInteger
import kairo.id.KairoId
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import osiris.schema.LlmSchema.LlmSchemaException

internal enum class LlmType {
  Boolean,
  Enum,
  Integer,
  List,
  Number,
  Object,
  Polymorphic,
  String,
}

internal fun parseLlmType(element: KAnnotatedElement, kClass: KClass<*>): LlmType {
  val annotation = element.findAnnotation<LlmSchema.Type>()
  if (annotation != null) {
    return when (annotation.type) {
      "boolean" -> LlmType.Boolean
      "integer" -> LlmType.Integer
      "number" -> LlmType.Number
      "string" -> LlmType.String
      else -> throw LlmSchemaException("Specified unsupported type ${annotation.type}.")
    }
  }
  when (kClass) {
    Boolean::class -> return LlmType.Boolean
    BigInteger::class, Int::class, Long::class, Short::class -> return LlmType.Integer
    List::class -> return LlmType.List
    BigDecimal::class, Double::class, Float::class -> return LlmType.Number
    KairoId::class, String::class -> return LlmType.String
  }
  if (kClass.java.isEnum) {
    return LlmType.Enum
  }
  if (kClass.isData) {
    return LlmType.Object
  }
  if (kClass.isSealed) {
    if (!kClass.hasAnnotation<JsonTypeInfo>()) {
      throw LlmSchemaException("Missing @${JsonTypeInfo::class.simpleName!!} on sealed (polymorphic) class.")
    }
    if (!kClass.hasAnnotation<JsonSubTypes>()) {
      throw LlmSchemaException("Missing @${JsonSubTypes::class.simpleName!!} on sealed (polymorphic) class.")
    }
    return LlmType.Polymorphic
  }
  throw LlmSchemaException(
    "Missing @${LlmSchema.Type::class.simpleName!!}" +
      " and the type could not be inferred.",
  )
}
