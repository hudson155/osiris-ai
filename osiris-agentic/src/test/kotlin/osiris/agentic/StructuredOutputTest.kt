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
import osiris.core.trace
import osiris.tracing.AgentEvent
import osiris.tracing.ChatEvent
import osiris.tracing.ExecutionEvent
import osiris.tracing.Trace

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class StructuredOutputTest {
  private val network: Network =
    network("network") {
      entrypoint = personCreator.name
      agents += personCreator
    }

  private val messages: List<UserMessage> =
    listOf(
      UserMessage("Jeff Hudson, 29, is a software engineer. He's also a pilot and an ultra trail runner."),
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
    response.convert<Person>().shouldBe(Person(name = "Jeff Hudson", age = 29))
  }

  private fun verifyTrace(trace: Trace) {
    with(trace.spans.map { it.details }) {
      shouldForExactly(1) { details ->
        details.shouldBeInstanceOf<ExecutionEvent>()
        details.network.shouldBe(network)
      }
      shouldForExactly(1) { details ->
        details.shouldBeInstanceOf<AgentEvent>()
        details.agent.shouldBe(personCreator)
      }
      shouldForExactly(1) { it.shouldBeInstanceOf<ChatEvent>() }
      shouldHaveSize(3)
    }
  }
}
