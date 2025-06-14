package osiris.agentic

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.UserMessage
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import osiris.core.convert
import osiris.core.response

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class MathTest {
  private val network: Network =
    network("network") {
      entrypoint = mathAgent.name
      agents += mathAgent
    }

  private val messages: List<UserMessage> =
    listOf(
      UserMessage("What's 2+2?"),
    )

  @Test
  fun test(): Unit = runTest {
    val events = network.run(messages).toList()
    verifyResponse(events.response())
  }

  private fun verifyResponse(response: AiMessage) {
    response.convert<String>().shouldBe("4")
  }
}
