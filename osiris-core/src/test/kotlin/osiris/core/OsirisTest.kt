package osiris.core

import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kairo.environmentVariableSupplier.DefaultEnvironmentVariableSupplier
import kairo.protectedString.ProtectedString
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import osiris.openAi.openAi
import osiris.openAi.openAiApiKey
import osiris.schema.OsirisSchema
import osiris.testing.execution
import osiris.testing.getMessages
import osiris.testing.getResponse
import osiris.testing.toolCall
import osiris.testing.verifyAiMessage
import osiris.testing.verifyMessages
import osiris.testing.verifyToolMessages

internal class OsirisTest {
  @Suppress("UnnecessaryLet")
  @OptIn(ProtectedString.Access::class)
  private val modelFactory: ModelFactory =
    modelFactory {
      openAiApiKey = DefaultEnvironmentVariableSupplier["OPEN_AI_API_KEY"]?.let { ProtectedString(it) }
    }

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
      verifyAiMessage {
        response = true
      }
    }
    osirisEvents.getResponse().shouldBe("4")
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
      verifyAiMessage {
        toolCall("weather", WeatherTool.Input("Calgary"))
        toolCall("weather", WeatherTool.Input("Edmonton"))
      }
      verifyToolMessages {
        execution("weather", WeatherTool.Output(temperature = "15 degrees Celsius", conditions = "Sunny"))
        execution("weather", WeatherTool.Output(temperature = "-30 degrees Celsius", conditions = "Snowing"))
      }
    }
    osirisEvents.getResponse().shouldBeNull()
  }

  @Test
  fun `structured output`(): Unit = runTest {
    val osirisEvents = osiris<Person>(
      model = modelFactory.openAi("gpt-4.1-nano"),
      tools = mapOf("weather" to WeatherTool),
      messages = listOf(
        UserMessage("Jeff Hudson, 29, is a software engineer. He's also a pilot and an ultra trail runner."),
        SystemMessage("Provide a JSON representation of the person matching this description."),
      ),
    ).toList()
    verifyMessages(osirisEvents.getMessages()) {
      verifyAiMessage {
        response = true
      }
    }
    osirisEvents.getResponse().shouldBe(Person(name = "Jeff Hudson", age = 29))
  }
}
