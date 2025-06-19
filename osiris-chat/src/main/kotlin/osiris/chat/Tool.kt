package osiris.chat

import dev.langchain4j.agent.tool.ToolExecutionRequest
import dev.langchain4j.agent.tool.ToolSpecification
import dev.langchain4j.data.message.ToolExecutionResultMessage
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kairo.lazySupplier.LazySupplier
import kairo.reflect.KairoType
import kairo.serialization.util.readValueSpecial
import osiris.core.llmMapper
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

  public val toolSpecification: LazySupplier<ToolSpecification> =
    LazySupplier {
      ToolSpecification.builder().apply {
        name(name)
        description.get()?.let { description(it) }
        parameters(LlmSchema.generate(inputType.kotlinClass))
      }.build()
    }

  public suspend fun execute(executionRequest: ToolExecutionRequest): ToolExecutionResultMessage {
    logger.debug { "Started Tool: $executionRequest." }
    val inputString = executionRequest.arguments()
    val input = checkNotNull(llmMapper.readValueSpecial(inputString, inputType))
    val outputString = execute(input)
    val executionResult = ToolExecutionResultMessage.from(executionRequest, outputString)
    logger.debug { "Ended Tool: $executionResult." }
    return executionResult
  }

  public abstract suspend fun execute(input: Input): String

  override fun toString(): String =
    "Tool(name=$name)"
}
