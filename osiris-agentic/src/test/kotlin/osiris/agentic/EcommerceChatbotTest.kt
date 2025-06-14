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
internal class EcommerceChatbotTest {
  private val network: Network =
    network("network") {
      entrypoint = ecommerceChatbot.name
      agents += ecommerceChatbot
      agents += ecommerceOrderTracker
    }

  private val messages: List<UserMessage> =
    listOf(
      UserMessage("Where are my orders? The IDs are ord_0 and ord_1."),
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
        Should say that ord_0 has not been shipped yet,
        and that ord_1 is in transit.
      """.trimIndent(),
    )
  }
}
