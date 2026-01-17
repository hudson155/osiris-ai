package osiris.schema

import kotlin.reflect.KClass

internal val error: StructuredErrorBuilder = StructuredErrorBuilder

internal object StructuredErrorBuilder {
  val nameAnnotation: String =
    "@${Structured::class.simpleName}.${Structured.Name::class.simpleName}"

  val discriminatorAnnotation: String =
    "@${Structured::class.simpleName}.${Structured.Discriminator::class.simpleName}"

  fun structuredOutput(kClass: KClass<*>): String =
    "Structured output ${kClass.qualifiedName}"
}
