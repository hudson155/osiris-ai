package osiris.core

import dev.langchain4j.agent.tool.ToolSpecification
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kairo.lazySupplier.LazySupplier
import kairo.reflect.KairoType
import kairo.serialization.util.readValueSpecial
import osiris.schema.LlmSchema
import osiris.tracing.ToolEvent
import osiris.tracing.trace

private val logger: KLogger = KotlinLogging.logger {}

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

  internal suspend fun execute(id: String, inputString: String): String {
    logger.debug { "Started tool: (name=$name, id=$id, input=$inputString)." }
    val input = checkNotNull(llmMapper.readValueSpecial(inputString, inputType))
    return trace({ ToolEvent(this, id, input, it) }) {
      execute(input)
    }.also { outputString ->
      logger.debug { "Ended tool: (name=$name, id=$id, output=$outputString)." }
    }
  }

  public abstract suspend fun execute(input: Input): String

  override fun toString(): String =
    "Tool(name=$name)"
}
