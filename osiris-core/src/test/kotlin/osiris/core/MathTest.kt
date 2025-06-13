package osiris.core

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import io.kotest.inspectors.shouldForExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.openAi.openAi
import osiris.tracing.ChatEvent
import osiris.tracing.Trace

internal class MathTest {
  private val messages: List<ChatMessage> = listOf(
    UserMessage("What's 2+2?"),
    SystemMessage("Do the math. Return only the answer (nothing else)."),
  )

  @Test
  fun test(): Unit = runTest {
    val events = llm(
      model = testModelFactory.openAi("gpt-4.1-nano"),
      messages = messages,
    ).toList()
    verifyResponse(events.response())
    // verifyTrace(trace)
  }

  private fun verifyResponse(response: AiMessage) {
    response.convert<String>().shouldBe("4")
  }

  private fun verifyTrace(trace: Trace) {
    with(trace.spans.map { it.details }) {
      shouldForExactly(1) { it.shouldBeInstanceOf<ChatEvent>() }
      shouldHaveSize(1)
    }
  }
}
