package osiris.chat

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.openAi.openAi
import osiris.tracing.EventLogger
import osiris.tracing.tracer

internal class MathTest {
  private val messages: List<ChatMessage> = listOf(
    UserMessage("What's 2+2?"),
    SystemMessage("Do the math. Return only the answer (nothing else)."),
  )

  @Test
  fun test(): Unit = runTest {
    val tracer = tracer {
      listener(EventLogger)
    }
    val response = llm(
      model = testModelFactory.openAi("gpt-4.1-nano"),
      messages = messages,
      tracer = tracer,
    )
    verifyResponse(response)
  }

  private fun verifyResponse(response: List<ChatMessage>) {
    response.convert<String>().shouldBe("4")
  }
}
