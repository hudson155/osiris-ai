package osiris.core

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.UserMessage
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.evaluator.evaluate
import osiris.openAi.openAi

internal class ToolsTest {
  @Test
  fun test(): Unit = runTest {
    val messages = mutableListOf<ChatMessage>(
      UserMessage("What's the weather in Calgary and Edmonton?"),
    )
    val response = llm(
      model = testModelFactory.openAi("gpt-4.1-nano"),
      tools = listOf(WeatherTool),
      messages = messages,
    ).get()
    evaluate(
      model = testModelFactory.openAi("o3-mini"),
      messages = messages + response,
      criteria = """
        Should say that the weather in Calgary is 15 degrees Celsius and sunny,
        and that the weather in Edmonton is -30 degrees Celsius and snowing.
      """.trimIndent(),
    )
  }
}
