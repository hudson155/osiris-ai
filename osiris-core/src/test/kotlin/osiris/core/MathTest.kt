package osiris.core

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.openAi.openAi

internal class MathTest {
  private val messages: List<ChatMessage> = listOf(
    UserMessage("What's 2+2?"),
    SystemMessage("Do the math. Return only the answer (nothing else)."),
  )

  @Test
  fun test(): Unit = runTest {
    val response = _root_ide_package_.osiris.core2.llm(
      model = testModelFactory.openAi("gpt-4.1-nano"),
      messages = messages,
    )
    verifyResponse(response)
  }

  private fun verifyResponse(response: List<ChatMessage>) {
    response.convert<String>().shouldBe("4")
  }
}
