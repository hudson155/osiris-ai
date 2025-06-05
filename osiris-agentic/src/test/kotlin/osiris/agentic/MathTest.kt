package osiris.agentic

import dev.langchain4j.data.message.UserMessage
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import osiris.core.convert

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class MathTest {
  private val network: Network =
    network {
      entrypoint = mathAgent.name
      agents += mathAgent
    }

  @Test
  fun test(): Unit = runTest {
    val messages = listOf(
      UserMessage("What's 2+2?"),
    )
    val flow = network.run(messages)
    val response = flow.onEach(::logEvent).getResponse()
    response.convert<String>().shouldBe("4")
  }
}
