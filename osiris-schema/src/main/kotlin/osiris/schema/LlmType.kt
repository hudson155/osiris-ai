package osiris.schema

import java.math.BigDecimal
import java.math.BigInteger
import kairo.id.KairoId
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

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
      else -> throw LlmSchema.LlmSchemaException("Specified unsupported type ${annotation.type}.")
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
    if (!kClass.hasAnnotation<LlmSchema.Polymorphic>()) {
      throw LlmSchema.LlmSchemaException(
        "Missing @${LlmSchema.Polymorphic::class.simpleName!!} on sealed (polymorphic) class.",
      )
    }
    return LlmType.Polymorphic
  }
  throw LlmSchema.LlmSchemaException(
    "Missing @${LlmSchema.Type::class.simpleName!!}" +
      " and the type could not be inferred.",
  )
}
