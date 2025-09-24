package osiris.agent.llm

import dev.langchain4j.agent.tool.ToolSpecification
import dev.langchain4j.model.chat.request.json.JsonObjectSchema
import kairo.reflect.KairoType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import osiris.schema.SchemaGenerator

public abstract class Tool<I : Any, O : Any>(
  public val name: String,
) {
  public val inputType: KairoType<I> = KairoType.from(Tool::class, 0, this::class)
  private val outputType: KairoType<O> = KairoType.from(Tool::class, 0, this::class)

  public open val description: String? =
    null

  @Suppress("UNCHECKED_CAST")
  public suspend fun run(context: LlmContext, inputString: String): String {
    val input = Json.decodeFromString(serializer(inputType.kotlinType) as KSerializer<I>, inputString)
    val output = run(context, input)
    return Json.encodeToString(serializer(outputType.kotlinType) as KSerializer<O>, output)
  }

  public abstract suspend fun run(context: LlmContext, input: I): O
}

public fun <I : Any> Tool<I, *>.specification(): ToolSpecification =
  ToolSpecification.builder().apply {
    name(name)
    description(description)
    parameters(SchemaGenerator.generate(serializer(inputType.kotlinType).descriptor) as JsonObjectSchema)
  }.build()
