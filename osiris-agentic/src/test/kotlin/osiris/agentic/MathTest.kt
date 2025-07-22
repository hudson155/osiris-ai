package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.UserMessage
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import osiris.chat.convert
import osiris.tracing.EventLogger

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class MathTest {
  private val network: Network =
    network("network") {
      entrypoint = mathAgent.name
      agents += mathAgent
      listener(EventLogger)
    }

  private val messages: List<UserMessage> =
    listOf(
      UserMessage("What's 2+2?"),
    )

  @Test
  fun test(): Unit = runTest {
    val response = network.run(messages).messages
    verifyResponse(response)
  }

  private fun verifyResponse(response: List<ChatMessage>) {
    response.convert<String>().shouldBe("4")
  }
}
