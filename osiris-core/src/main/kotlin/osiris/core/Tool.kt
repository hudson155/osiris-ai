package osiris.core

import dev.langchain4j.agent.tool.ToolExecutionRequest
import dev.langchain4j.agent.tool.ToolSpecification
import dev.langchain4j.data.message.ToolExecutionResultMessage
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kairo.lazySupplier.LazySupplier
import kairo.reflect.KairoType
import kairo.serialization.util.readValueSpecial
import osiris.schema.LlmSchema

private val logger: KLogger = KotlinLogging.logger {}

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

  internal val toolSpecification: LazySupplier<ToolSpecification> =
    LazySupplier {
      ToolSpecification.builder().apply {
        name(name)
        description.get()?.let { description(it) }
        parameters(LlmSchema.generate(inputType.kotlinClass))
      }.build()
    }

  internal suspend fun execute(executionRequest: ToolExecutionRequest): ToolExecutionResultMessage {
    logger.debug { "Started tool: $executionRequest." }
    val inputString = executionRequest.arguments()
    val input = checkNotNull(llmMapper.readValueSpecial(inputString, inputType))
    val outputString = execute(input)
    val executionResponse = ToolExecutionResultMessage.from(executionRequest, outputString)
    logger.debug { "Ended tool: $executionResponse." }
    return executionResponse
  }

  internal abstract suspend fun execute(input: Input): String

  override fun toString(): String =
    "Tool(name=$name)"
}
