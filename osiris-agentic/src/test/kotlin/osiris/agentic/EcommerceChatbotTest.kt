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
internal class EcommerceChatbotTest {
  private val network: Network =
    network("network") {
      entrypoint = ecommerceChatbot.name
      agents += ecommerceChatbot
      agents += ecommerceOrderTracker
      listener(EventLogger)
    }

  private val messages: List<UserMessage> =
    listOf(
      UserMessage("Where are my orders? The IDs are ord_0 and ord_1."),
    )

  @Test
  fun test(): Unit = runTest {
    val response = network.run(messages).messages
    verifyResponse(response)
  }

  private suspend fun verifyResponse(response: List<ChatMessage>) {
    evaluate(
      model = testModelFactory.openAi("o4-mini"),
      messages = messages + response,
      criteria = """
        Should say that ord_0 has not been shipped yet,
        and that ord_1 is in transit.
      """.trimIndent(),
    )
  }
}
