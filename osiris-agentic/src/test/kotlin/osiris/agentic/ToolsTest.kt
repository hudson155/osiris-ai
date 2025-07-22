package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.UserMessage
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import osiris.evaluator.evaluate
import osiris.openAi.openAi
import osiris.tracing.EventLogger

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ToolsTest {
  private val network: Network =
    network("network") {
      entrypoint = weatherAgent.name
      agents += weatherAgent
      listener(EventLogger)
    }

  private val messages: List<UserMessage> =
    listOf(
      UserMessage("What's the weather in Calgary and Edmonton?"),
    )

  @Test
  fun test(): Unit = runTest {
    val response = network.run(messages).response
    verifyResponse(response)
  }

  private suspend fun verifyResponse(response: List<ChatMessage>) {
    evaluate(
      model = testModelFactory.openAi("o4-mini"),
      messages = messages + response,
      criteria = """
        Should say that the weather in Calgary is 15 degrees Celsius and sunny,
        and that the weather in Edmonton is -30 degrees Celsius and snowing.
      """.trimIndent(),
    )
  }
}
