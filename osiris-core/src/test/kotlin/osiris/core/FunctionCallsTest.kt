package osiris.core

import dev.langchain4j.data.message.UserMessage
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import osiris.evaluator.evaluate
import osiris.openAi.openAi
import osiris.schema.OsirisSchema
import osiris.testing.toolCall
import osiris.testing.toolMessage
import osiris.testing.verifyMessages
import osiris.testing.verifyResponse
import osiris.testing.verifyToolCalls
import osiris.testing.verifyToolMessages

internal class FunctionCallsTest {
  internal object WeatherTool : OsirisTool<WeatherTool.Input, WeatherTool.Output>("weather") {
    data class Input(
      @OsirisSchema.Description("The city to get the weather for. Only the city name.")
      val location: String,
    )

    data class Output(
      val temperature: String,
      val conditions: String,
    )

    override suspend fun invoke(input: Input): Output =
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

  @Test
  fun `execute tools = true`(): Unit = runTest {
    val osirisEvents = osiris(
      model = modelFactory.openAi("gpt-4.1-nano"),
      tools = mapOf("weather" to WeatherTool),
      messages = listOf(
        UserMessage("What's the weather in Calgary and Edmonton?"),
      ),
    ).toList()
    verifyMessages(osirisEvents.getMessages()) {
      verifyToolCalls {
        toolCall("weather", WeatherTool.Input("Calgary"))
        toolCall("weather", WeatherTool.Input("Edmonton"))
      }
      verifyToolMessages {
        toolMessage("weather", WeatherTool.Output(temperature = "15 degrees Celsius", conditions = "Sunny"))
        toolMessage("weather", WeatherTool.Output(temperature = "-30 degrees Celsius", conditions = "Snowing"))
      }
      verifyResponse()
    }
    evaluate(
      model = modelFactory.openAi("o3-mini"),
      response = osirisEvents.getMessages().getResponse().shouldNotBeNull(),
      criteria = """
        Should say the weather in Calgary is 15 degrees Celsius and sunny,
        and that the weather in Edmonton is -30 degrees Celsius and snowing.
      """.trimIndent(),
    )
  }

  @Test
  fun `execute tools = false`(): Unit = runTest {
    val osirisEvents = osiris(
      model = modelFactory.openAi("gpt-4.1-nano"),
      tools = mapOf("weather" to WeatherTool),
      messages = listOf(
        UserMessage("What's the weather in Calgary and Edmonton?"),
      ),
      executeTools = false,
    ).toList()
    verifyMessages(osirisEvents.getMessages()) {
      verifyToolCalls {
        toolCall("weather", WeatherTool.Input("Calgary"))
        toolCall("weather", WeatherTool.Input("Edmonton"))
      }
    }
    osirisEvents.getMessages().getResponse().shouldBeNull()
  }
}
