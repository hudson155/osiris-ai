package osiris.agentic

import dev.langchain4j.data.message.UserMessage
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import osiris.core.convert
import osiris.evaluator.evaluate
import osiris.openAi.openAi

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class EcommerceChatbotTest {
  private val network: Network =
    network {
      entrypoint = ecommerceChatbot.name
      agents += ecommerceChatbot
      agents += ecommerceOrderTracker
    }

  @Test
  fun test(): Unit = runTest {
    val flow = network.run(
      messages = listOf(
        UserMessage("Where are my orders? The IDs are ord_0 and ord_1."),
      ),
    )
    val response = flow.onEach(::logEvent).getResponse()
    evaluate(
      model = testModelFactory.openAi("o3-mini"),
      response = response.convert<String>(),
      criteria = """
        Should say that ord_0 has not been shipped yet,
        and that ord_1 is in transit.
      """.trimIndent(),
    )
  }
}
