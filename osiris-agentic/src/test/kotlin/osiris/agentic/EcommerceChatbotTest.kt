package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.UserMessage
import io.kotest.inspectors.shouldForExactly
import io.kotest.inspectors.shouldForOne
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import osiris.evaluator.evaluate
import osiris.openAi.openAi
import osiris.tracing.AgentEvent
import osiris.tracing.ChatEvent
import osiris.tracing.ExecutionEvent
import osiris.tracing.ToolEvent
import osiris.tracing.Trace
import osiris.tracing.trace

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
    val (response, trace) = trace {
      network.run(messages)
    }
    verifyResponse(response)
    verifyTrace(trace)
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

  private fun verifyTrace(trace: Trace) {
    with(trace.spans.map { it.details }) {
      shouldForExactly(1) { details ->
        details.shouldBeInstanceOf<ExecutionEvent>()
        details.network.shouldBe(network)
      }
      shouldForExactly(1) { details ->
        details.shouldBeInstanceOf<AgentEvent>()
        details.agent.shouldBe(ecommerceChatbot)
      }
      shouldForExactly(2) { details ->
        details.shouldBeInstanceOf<AgentEvent>()
        details.agent.shouldBe(ecommerceOrderTracker)
      }
      shouldForExactly(6) { it.shouldBeInstanceOf<ChatEvent>() }
      shouldForOne { details ->
        details.shouldBeInstanceOf<ToolEvent>()
        details.tool.shouldBe(TrackOrderTool)
        details.input.shouldBe(TrackOrderTool.Input("ord_0"))
      }
      shouldForOne { details ->
        details.shouldBeInstanceOf<ToolEvent>()
        details.tool.shouldBe(TrackOrderTool)
        details.input.shouldBe(TrackOrderTool.Input("ord_1"))
      }
      shouldForExactly(2) { details ->
        details.shouldBeInstanceOf<ToolEvent>()
        details.tool.shouldBeInstanceOf<Consult>()
      }
      shouldHaveSize(14)
    }
  }
}
