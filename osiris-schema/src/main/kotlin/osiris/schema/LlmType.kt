package osiris.schema

import java.math.BigDecimal
import java.math.BigInteger
import kairo.id.KairoId
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import osiris.schema.LlmSchema.LlmSchemaException

internal enum class LlmType {
  Boolean,
  Integer,
  List,
  Number,
  Object,
  String,
}

internal fun parseType(element: KAnnotatedElement?, type: KType): LlmType {
  val annotation = element?.findAnnotation<LlmSchema.Type>()
  if (annotation != null) {
    return when (annotation.type) {
      "boolean" -> LlmType.Boolean
      "integer" -> LlmType.Integer
      "number" -> LlmType.Number
      "string" -> LlmType.String
      else -> throw LlmSchemaException("Specified unsupported type ${annotation.type}.")
    }
  }
  if (type.classifier is KClass<*> && (type.classifier as KClass<*>).isData) return LlmType.Object
  return when (type.classifier) {
    Boolean::class -> LlmType.Boolean
    BigInteger::class, Int::class, Long::class, Short::class -> LlmType.Integer
    List::class -> LlmType.List
    BigDecimal::class, Double::class, Float::class -> LlmType.Number
    KairoId::class, String::class -> LlmType.String
    else -> throw LlmSchemaException(
      "Missing @${LlmSchema.Type::class.simpleName!!}" +
        " and the type could not be inferred.",
    )
  }
}
