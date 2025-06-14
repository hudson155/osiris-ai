package osiris.agentic

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.UserMessage
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import osiris.core.response
import osiris.evaluator.evaluate
import osiris.openAi.openAi

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
    val events = network.run(messages).toList()
    verifyResponse(events.response())
  }

  private suspend fun verifyResponse(response: AiMessage) {
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
