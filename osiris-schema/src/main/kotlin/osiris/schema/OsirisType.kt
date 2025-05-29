package osiris.schema

import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation

internal enum class OsirisType {
  Boolean,
  Integer,
  Number,
  String,
}

internal fun parseType(kClass: KClass<*>, param: KParameter): OsirisType {
  val annotation = param.findAnnotation<OsirisSchema.Type>()
  if (annotation != null) {
    return when (val type = annotation.type) {
      "boolean" -> OsirisType.Boolean
      "integer" -> OsirisType.Integer
      "number" -> OsirisType.Number
      "string" -> OsirisType.String
      else -> throw IllegalArgumentException("Unsupported type: $type.")
    }
  }
  return when (param.type.classifier) {
    Boolean::class -> OsirisType.Boolean
    BigInteger::class, Int::class, Long::class, Short::class -> OsirisType.Integer
    BigDecimal::class, Double::class, Float::class -> OsirisType.Number
    String::class -> OsirisType.String
    else -> throw IllegalArgumentException(
      "Osiris schema for ${kClass.qualifiedName!!}::${param.name!!}" +
        " is missing @${OsirisSchema.Type::class.simpleName!!}," +
        " and the type could not be inferred."
    )
  }
}
