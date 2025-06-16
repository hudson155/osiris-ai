package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.UserMessage
import kairo.protectedString.ProtectedString
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import osiris.evaluator.evaluate
import osiris.langfuse.Langfuse
import osiris.langfuseTracing.trace
import osiris.openAi.openAi
import osiris.tracing.EventLogger

@OptIn(ProtectedString.Access::class)
private val langfuse: Langfuse =
  Langfuse(
    url = "https://us.cloud.langfuse.com/api/public/",
    publicKey = "pk-lf-bd38df69-84c7-43f5-a11a-3ccf8a542d37",
    secretKey = ProtectedString("sk-lf-e79dcff7-25af-477d-9996-28d77cfdc52a"),
  )

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class EcommerceChatbotTest {
  private val network: Network =
    network("network") {
      entrypoint = ecommerceChatbot.name
      agents += ecommerceChatbot
      agents += ecommerceOrderTracker
      listener(EventLogger)
      listener(langfuse.trace())
    }

  private val messages: List<UserMessage> =
    listOf(
      UserMessage("Where are my orders? The IDs are ord_0 and ord_1."),
    )

  @Test
  fun test(): Unit = runTest {
    val response = network.run(messages)
    verifyResponse(response)
  }

  private suspend fun verifyResponse(response: List<ChatMessage>) {
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
