package osiris.core

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.UserMessage
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.evaluator.evaluate
import osiris.openAi.openAi

internal class ToolsTest {
  private val messages: List<ChatMessage> =
    listOf(
      UserMessage("What's the weather in Calgary and Edmonton?"),
    )

  @Test
  fun test(): Unit = runTest {
    val response = llm(
      model = testModelFactory.openAi("gpt-4.1-nano"),
      messages = messages,
      tools = listOf(WeatherTool),
    )
    verifyResponse(response)
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
}
