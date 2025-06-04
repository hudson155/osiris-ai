package osiris.core

import dev.langchain4j.agent.tool.ToolSpecification
import kairo.reflect.KairoType
import kairo.serialization.util.kairoWriteSpecial
import kairo.serialization.util.readValueSpecial
import osiris.schema.llmSchema

public abstract class Tool<in Input : Any, out Output : Any>(
  public val name: String,
) {
  private val inputType: KairoType<Input> = KairoType.from(Tool::class, 0, this::class)
  private val outputType: KairoType<Output> = KairoType.from(Tool::class, 1, this::class)

  public open val description: String? = null

  public val toolSpecification: ToolSpecification by lazy {
    return@lazy ToolSpecification.builder().apply {
      name(name)
      if (description != null) description(description)
      parameters(llmSchema(inputType.kotlinClass))
    }.build()
  }

  public suspend fun execute(string: String): String {
    val input = checkNotNull(llmMapper.readValueSpecial(string, inputType))
    val output = execute(input)
    return llmMapper.kairoWriteSpecial(output, outputType)
  }

  public abstract suspend fun execute(input: Input): Output
}
