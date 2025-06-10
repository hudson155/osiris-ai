package osiris.agentic

import io.kotest.assertions.fail
import kairo.lazySupplier.LazySupplier
import osiris.agentic.WeatherTool.Input
import osiris.agentic.WeatherTool.Output
import osiris.core.Tool
import osiris.schema.LlmSchema

internal object WeatherTool : Tool<Input, Output>("weather") {
  internal data class Input(
    @LlmSchema.Description("The city to get the weather for. Only the city name.")
    val location: String,
  )

  internal data class Output(
    val temperature: String,
    val conditions: String,
  )

  override val description: LazySupplier<String> =
    LazySupplier { "Gets the weather." }

  override suspend fun execute(input: Input): Output =
    when (val location = input.location) {
      "Calgary" -> Output(
        temperature = "15 degrees Celsius",
        conditions = "Sunny",
      )
      "Edmonton" -> Output(
        temperature = "-30 degrees Celsius",
        conditions = "Snowing",
      )
      else -> fail("Unknown location: $location.")
    }
}
