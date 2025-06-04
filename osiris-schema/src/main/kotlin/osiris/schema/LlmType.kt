package osiris.schema

import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation

internal enum class LlmType {
  Boolean,
  Integer,
  Number,
  String,
}

internal fun parseType(kClass: KClass<*>, param: KParameter): LlmType {
  val annotation = param.findAnnotation<LlmSchema.Type>()
  if (annotation != null) {
    return when (val type = annotation.type) {
      "boolean" -> LlmType.Boolean
      "integer" -> LlmType.Integer
      "number" -> LlmType.Number
      "string" -> LlmType.String
      else -> throw IllegalArgumentException(
        "LLM schema for ${kClass.qualifiedName!!}::${param.name!!}" +
          " specified an unsupported type: $type.",
      )
    }
  }
  return when (param.type.classifier) {
    Boolean::class -> LlmType.Boolean
    BigInteger::class, Int::class, Long::class, Short::class -> LlmType.Integer
    BigDecimal::class, Double::class, Float::class -> LlmType.Number
    String::class -> LlmType.String
    else -> throw IllegalArgumentException(
      "LLM schema for ${kClass.qualifiedName!!}::${param.name!!}" +
        " is missing @${LlmSchema.Type::class.simpleName!!}," +
        " and the type could not be inferred.",
    )
  }
}
