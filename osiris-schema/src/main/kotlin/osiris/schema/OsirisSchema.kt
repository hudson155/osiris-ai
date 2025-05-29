package osiris.schema

import dev.langchain4j.model.chat.request.json.JsonObjectSchema
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.valueParameters

public object OsirisSchema {
  @Target(AnnotationTarget.VALUE_PARAMETER)
  public annotation class Type(val type: String)

  @Target(AnnotationTarget.VALUE_PARAMETER)
  public annotation class Description(val description: String)
}

public fun osirisSchema(kClass: KClass<*>): JsonObjectSchema {
  require(kClass.isData) {
    "Osiris schema ${kClass.qualifiedName!!}" +
      " must be a data class or data object."
  }
  return JsonObjectSchema.builder().apply {
    val required: MutableList<String> = mutableListOf()
    getAllParams(kClass).forEach { param ->
      val name = checkNotNull(param.name)
      val type = parseType(kClass, param)
      val description = parseDescription(param)
      when (type) {
        OsirisType.Boolean -> addBooleanProperty(name, description)
        OsirisType.Integer -> addIntegerProperty(name, description)
        OsirisType.Number -> addNumberProperty(name, description)
        OsirisType.String -> addStringProperty(name, description)
      }
      require(!param.isOptional) {
        "Osiris schema for ${kClass.qualifiedName!!}::${param.name!!}" +
          " must not be optional."
      }
      if (!param.type.isMarkedNullable) {
        required += name
      }
    }
    required(required)
  }.build()
}

private fun parseDescription(param: KParameter): String? {
  val annotation = param.findAnnotation<OsirisSchema.Description>() ?: return null
  return annotation.description
}

private fun getAllParams(kClass: KClass<*>): List<KParameter> {
  if (kClass.objectInstance != null) return emptyList()
  val constructor = checkNotNull(kClass.primaryConstructor) {
    "Data classes always have primary constructors."
  }
  return constructor.valueParameters
}
