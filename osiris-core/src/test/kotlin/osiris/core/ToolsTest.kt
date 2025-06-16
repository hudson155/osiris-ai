package osiris.core

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.UserMessage
import kairo.protectedString.ProtectedString
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.evaluator.evaluate
import osiris.langfuse.Langfuse
import osiris.langfuseTracing.trace
import osiris.openAi.openAi
import osiris.tracing.EventLogger
import osiris.tracing.tracer

@OptIn(ProtectedString.Access::class)
private val langfuse: Langfuse =
  Langfuse(
    url = "https://us.cloud.langfuse.com/api/public/",
    publicKey = "pk-lf-bd38df69-84c7-43f5-a11a-3ccf8a542d37",
    secretKey = ProtectedString("sk-lf-e79dcff7-25af-477d-9996-28d77cfdc52a"),
  )

internal class ToolsTest {
  private val messages: List<ChatMessage> =
    listOf(
      UserMessage("What's the weather in Calgary and Edmonton?"),
    )

  @Test
  fun test(): Unit = runTest {
    val tracer = tracer {
      listener(EventLogger)
      listener(langfuse.trace())
    }
    val response = llm(
      model = testModelFactory.openAi("gpt-4.1-nano"),
      messages = messages,
      tools = listOf(WeatherTool),
      tracer = tracer,
    )
    verifyResponse(response)
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
}
