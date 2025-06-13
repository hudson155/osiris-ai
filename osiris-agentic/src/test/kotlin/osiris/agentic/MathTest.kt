package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.UserMessage
import io.kotest.inspectors.shouldForExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import osiris.core.convert
import osiris.tracing.AgentEvent
import osiris.tracing.ChatEvent
import osiris.tracing.ExecutionEvent
import osiris.tracing.Trace
import osiris.tracing.trace

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
    val (response, trace) = trace {
      network.run(messages)
    }
    verifyResponse(response)
    verifyTrace(trace)
  }

  private fun verifyResponse(response: List<ChatMessage>) {
    response.convert<String>().shouldBe("4")
  }

  private fun verifyTrace(trace: Trace) {
    with(trace.spans.map { it.details }) {
      shouldForExactly(1) { details ->
        details.shouldBeInstanceOf<ExecutionEvent>()
        details.network.shouldBe(network)
      }
      shouldForExactly(1) { details ->
        details.shouldBeInstanceOf<AgentEvent>()
        details.agent.shouldBe(mathAgent)
      }
      shouldForExactly(1) { it.shouldBeInstanceOf<ChatEvent>() }
      shouldHaveSize(3)
    }
  }
}
