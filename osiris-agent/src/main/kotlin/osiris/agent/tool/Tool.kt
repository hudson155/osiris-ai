package osiris.agent.tool

import dev.langchain4j.agent.tool.ToolSpecification
import dev.langchain4j.model.chat.request.json.JsonObjectSchema
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.utils.io.CancellationException
import kairo.reflect.KairoType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import osiris.agent.Context
import osiris.schema.SchemaGenerator

private val logger: KLogger = KotlinLogging.logger {}

public abstract class Tool<I : Any, O : Any>(
  public val name: String,
) {
  public val inputType: KairoType<I> = KairoType.from(Tool::class, 0, this::class)
  public val outputType: KairoType<O> = KairoType.from(Tool::class, 1, this::class)

  public open val description: String? =
    null

  @Suppress("UNCHECKED_CAST")
  public suspend fun run(context: Context, inputString: String): String {
    val input = Json.decodeFromString(serializer(inputType.kotlinType) as KSerializer<I>, inputString)
    try {
      val output = run(context, input)
      return Json.encodeToString(serializer(outputType.kotlinType) as KSerializer<O>, output)
    } catch (e: ToolException) {
      logger.debug(e) { "Handled tool exception." }
      return e.message
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      logger.error(e) { "Unhandled tool exception." }
      return "Error"
    }
  }

  public abstract suspend fun run(context: Context, input: I): O
}

public fun <I : Any> Tool<I, *>.specification(): ToolSpecification =
  ToolSpecification.builder().apply {
    name(name)
    description(description)
    parameters(SchemaGenerator.generate(serializer(inputType.kotlinType).descriptor) as JsonObjectSchema)
  }.build()
