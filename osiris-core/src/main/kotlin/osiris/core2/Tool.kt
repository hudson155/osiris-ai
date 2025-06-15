package osiris.core2

import dev.langchain4j.agent.tool.ToolExecutionRequest
import dev.langchain4j.agent.tool.ToolSpecification
import dev.langchain4j.data.message.ToolExecutionResultMessage
import kairo.lazySupplier.LazySupplier
import kairo.reflect.KairoType
import osiris.schema.LlmSchema

/**
 * Implementations of this class are made available to the LLM as tools.
 * The name and description are made available to the LLM,
 * as well as the Osiris input schema.
 */
public abstract class Tool<in Input : Any>(
  public val name: String,
) {
  private val inputType: KairoType<Input> = KairoType.from(Tool::class, 0, this::class)

  public open val description: LazySupplier<out String?> =
    LazySupplier { null }

  public val toolSpecification: LazySupplier<ToolSpecification> =
    LazySupplier {
      ToolSpecification.builder().apply {
        name(name)
        description.get()?.let { description(it) }
        parameters(LlmSchema.generate(inputType.kotlinClass))
      }.build()
    }

  public abstract suspend fun execute(executionRequest: ToolExecutionRequest): ToolExecutionResultMessage

  override fun toString(): String =
    "Tool(name=$name)"
}
