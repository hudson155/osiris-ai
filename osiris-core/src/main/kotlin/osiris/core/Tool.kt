package osiris.core

import dev.langchain4j.agent.tool.ToolExecutionRequest
import dev.langchain4j.agent.tool.ToolSpecification
import dev.langchain4j.data.message.ToolExecutionResultMessage
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kairo.lazySupplier.LazySupplier
import kairo.reflect.KairoType
import kairo.serialization.util.readValueSpecial
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import osiris.core2.llmMapper
import osiris.event.Event
import osiris.event.onMessage
import osiris.schema.LlmSchema

private val logger: KLogger = KotlinLogging.logger {}

/**
 * Implementations of this class are made available to the LLM as tools.
 * The name and description are made available to the LLM,
 * as well as the Osiris input schema.
 *
 * Most implementations should use [SimpleTool],
 * which returns a string instead of a Flow.
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

  public fun execute(executionRequest: ToolExecutionRequest): Flow<Event> {
    val id = executionRequest.id()
    val inputString = executionRequest.arguments()
    logger.debug { "Started tool: (name=$name, id=$id, input=$inputString)." }
    val input = checkNotNull(llmMapper.readValueSpecial(inputString, inputType))
    val flow = execute(executionRequest, input)
    var executionResult: ToolExecutionResultMessage? = null
    return flow
      .onMessage { message ->
        if (message !is ToolExecutionResultMessage) return@onMessage
        if (message.id() != id) return@onMessage
        check(executionResult == null)
        executionResult = message
      }
      .onCompletion {
        checkNotNull(executionResult)
        logger.debug { "Ended tool: (name=$name, id=$id, output=${executionResult.text()})." }
      }
  }

  public abstract fun execute(executionRequest: ToolExecutionRequest, input: Input): Flow<Event>

  override fun toString(): String =
    "Tool(name=$name)"
}
