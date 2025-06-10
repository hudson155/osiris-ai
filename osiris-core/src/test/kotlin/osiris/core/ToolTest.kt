package osiris.core

import dev.langchain4j.agent.tool.ToolSpecification
import dev.langchain4j.model.chat.request.json.JsonObjectSchema
import io.kotest.assertions.shouldFailWithMessage
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class ToolTest {
  @Test
  fun name(): Unit = runTest {
    WeatherTool.name.shouldBe("weather")
  }

  @Test
  fun description(): Unit = runTest {
    WeatherTool.description.get().shouldBe("Gets the weather.")
  }

  @Test
  fun toolSpecification(): Unit = runTest {
    WeatherTool.toolSpecification.get()
      .shouldBe(
        ToolSpecification.builder().apply {
          name("weather")
          description("Gets the weather.")
          parameters(
            JsonObjectSchema.builder().apply {
              addStringProperty("location", "The city to get the weather for. Only the city name.")
              required("location")
            }.build(),
          )
        }.build(),
      )
  }

  @Test
  fun invoke(): Unit = runTest {
    WeatherTool.execute(WeatherTool.Input(location = "Calgary"))
      .shouldBe(WeatherTool.Output(temperature = "15 degrees Celsius", conditions = "Sunny"))

    WeatherTool.execute(WeatherTool.Input(location = "Edmonton"))
      .shouldBe(WeatherTool.Output(temperature = "-30 degrees Celsius", conditions = "Snowing"))

    shouldFailWithMessage("Unknown location: New York.") {
      WeatherTool.execute(WeatherTool.Input("New York"))
    }
  }
}
