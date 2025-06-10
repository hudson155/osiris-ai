package osiris.core

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import io.kotest.matchers.collections.shouldMatchEach
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.openAi.openAi
import osiris.span.ChatEvent
import osiris.span.Span

internal class MathTest {
  private val messages: List<ChatMessage> = listOf(
    UserMessage("What's 2+2?"),
    SystemMessage("Do the math. Return only the answer (nothing else)."),
  )

  @Test
  fun test(): Unit = runTest {
    val (response, trace) = llm(
      model = testModelFactory.openAi("gpt-4.1-nano"),
      messages = messages,
    )
    verifyResponse(response)
    verifyTrace(trace)
  }

  private fun verifyResponse(response: List<ChatMessage>) {
    response.convert<String>().shouldBe("4")
  }

  private fun verifyTrace(trace: List<Span<*>>) {
    trace.shouldMatchEach(
      { it.details.shouldBeInstanceOf<ChatEvent>() },
    )
  }
}
