package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.UserMessage
import io.kotest.inspectors.shouldForExactly
import io.kotest.inspectors.shouldForOne
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import osiris.evaluator.evaluate
import osiris.openAi.openAi
import osiris.span.AgentEvent
import osiris.span.ChatEvent
import osiris.span.ExecutionEvent
import osiris.span.Span
import osiris.span.ToolEvent

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ToolsTest {
  private val network: Network =
    network("network") {
      entrypoint = weatherAgent.name
      agents += weatherAgent
    }

  private val messages: List<UserMessage> =
    listOf(
      UserMessage("What's the weather in Calgary and Edmonton?"),
    )

  @Test
  fun test(): Unit = runTest {
    val (response, trace) = network.run(messages)
    verifyResponse(response)
    verifyTrace(trace)
  }

  private suspend fun verifyResponse(response: List<ChatMessage>) {
    evaluate(
      model = testModelFactory.openAi("o3-mini"),
      messages = messages + response,
      criteria = """
        Should say that the weather in Calgary is 15 degrees Celsius and sunny,
        and that the weather in Edmonton is -30 degrees Celsius and snowing.
      """.trimIndent(),
    )
  }

  private fun verifyTrace(trace: List<Span<*>>) {
    with(trace.map { it.details }) {
      shouldForExactly(1) { details ->
        details.shouldBeInstanceOf<ExecutionEvent>()
        details.network.shouldBe(network)
      }
      shouldForExactly(1) { details ->
        details.shouldBeInstanceOf<AgentEvent>()
        details.agent.shouldBe(weatherAgent)
      }
      shouldForExactly(2) { it.shouldBeInstanceOf<ChatEvent>() }
      shouldForOne { details ->
        details.shouldBeInstanceOf<ToolEvent>()
        details.tool.shouldBe(WeatherTool)
        details.input.shouldBe(WeatherTool.Input("Calgary"))
      }
      shouldForOne { details ->
        details.shouldBeInstanceOf<ToolEvent>()
        details.tool.shouldBe(WeatherTool)
        details.input.shouldBe(WeatherTool.Input("Edmonton"))
      }
      shouldHaveSize(6)
    }
  }
}
