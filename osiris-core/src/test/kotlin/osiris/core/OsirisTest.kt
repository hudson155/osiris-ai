package osiris.core

import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import io.kotest.matchers.shouldBe
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

internal class OsirisTest {
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

  @OsirisSchema.SchemaName("person")
  internal data class Person(
    val name: String,
    val age: Int,
  )

  @Test
  fun simple(): Unit = runTest {
    val osirisEvents = osiris(
      model = modelFactory.openAi("gpt-4.1-nano"),
      messages = listOf(
        UserMessage("What's 2+2?"),
        SystemMessage("Do the math. Return only the answer (nothing else)."),
      ),
    ).toList()
    verifyMessages(osirisEvents.getMessages()) {
      verifyResponse()
    }
    osirisEvents.getMessages().getResponse().shouldBe("4")
  }

  @Test
  fun `function calls`(): Unit = runTest {
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
      response = osirisEvents.getMessages().getResponse(),
      criteria = """
        Should say the weather in Calgary is 15 degrees Celsius and sunny,
        and that the weather in Edmonton is -30 degrees Celsius and snowing.
      """.trimIndent(),
    )
  }

  @Test
  fun `structured output`(): Unit = runTest {
    val osirisEvents = osiris(
      model = modelFactory.openAi("gpt-4.1-nano"),
      responseType = Person::class,
      messages = listOf(
        UserMessage("Jeff Hudson, 29, is a software engineer. He's also a pilot and an ultra trail runner."),
        SystemMessage("Provide a JSON representation of the person matching this description."),
      ),
    ).toList()
    verifyMessages(osirisEvents.getMessages()) {
      verifyResponse()
    }
    osirisEvents.getMessages().getResponseAs<Person>()
      .shouldBe(Person(name = "Jeff Hudson", age = 29))
  }
}
