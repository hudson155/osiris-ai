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
    WeatherTool.description.shouldBe("Gets the weather.")
  }

  @Test
  fun toolSpecification(): Unit = runTest {
    WeatherTool.toolSpecification
      .shouldBe(
        ToolSpecification.builder().apply {
          name("weather")
          parameters(
            JsonObjectSchema.builder().apply {
              addStringProperty("location", "The city to get the weather for. Only the city name.")
              required("location")
            }.build()
          )
        }.build()
      )
  }

  @Test
  fun `invoke (typed)`(): Unit = runTest {
    WeatherTool.execute(WeatherTool.Input(location = "Calgary"))
      .shouldBe(WeatherTool.Output(temperature = "15 degrees Celsius", conditions = "Sunny"))

    WeatherTool.execute(WeatherTool.Input(location = "Edmonton"))
      .shouldBe(WeatherTool.Output(temperature = "-30 degrees Celsius", conditions = "Snowing"))

    shouldFailWithMessage("Unknown location: New York.") {
      WeatherTool.execute(WeatherTool.Input("New York"))
    }
  }

  @Test
  fun `invoke (string)`(): Unit = runTest {
    WeatherTool.execute("{\"location\":\"Calgary\"}")
      .shouldBe("{\"temperature\":\"15 degrees Celsius\",\"conditions\":\"Sunny\"}")

    WeatherTool.execute("{\"location\":\"Edmonton\"}")
      .shouldBe("{\"temperature\":\"-30 degrees Celsius\",\"conditions\":\"Snowing\"}")

    shouldFailWithMessage("Unknown location: New York.") {
      WeatherTool.execute("{\"location\":\"New York\"}")
    }
  }
}
