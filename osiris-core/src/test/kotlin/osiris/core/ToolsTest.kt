package osiris.core

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.UserMessage
import io.kotest.inspectors.shouldForExactly
import io.kotest.inspectors.shouldForOne
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.evaluator.evaluate
import osiris.openAi.openAi
import osiris.tracing.ChatEvent
import osiris.tracing.ToolEvent
import osiris.tracing.Trace

internal class ToolsTest {
  private val messages: List<ChatMessage> =
    listOf(
      UserMessage("What's the weather in Calgary and Edmonton?"),
    )

  @Test
  fun test(): Unit = runTest {
    val events = llm(
      model = testModelFactory.openAi("gpt-4.1-nano"),
      messages = messages,
      tools = listOf(WeatherTool),
    ).toList()
    verifyResponse(events.messages().toList())
    // verifyTrace(trace)
  }

  private suspend fun verifyResponse(messages: List<ChatMessage>) {
    evaluate(
      model = testModelFactory.openAi("o3-mini"),
      messages = this.messages + messages,
      criteria = """
        Should say that the weather in Calgary is 15 degrees Celsius and sunny,
        and that the weather in Edmonton is -30 degrees Celsius and snowing.
      """.trimIndent(),
    )
  }

  private fun verifyTrace(trace: Trace) {
    with(trace.spans.map { it.details }) {
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
      shouldHaveSize(4)
    }
  }
}
