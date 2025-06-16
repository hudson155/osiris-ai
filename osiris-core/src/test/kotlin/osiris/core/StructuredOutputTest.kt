package osiris.core

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.openAi.openAi
import osiris.tracing.EventLogger
import osiris.tracing.tracer

internal class StructuredOutputTest {
  private val messages: List<ChatMessage> =
    listOf(
      UserMessage("Jeff Hudson, 29, is a software engineer. He's also a pilot and an ultra trail runner."),
      SystemMessage("Provide a JSON representation of the person matching this description."),
    )

  @Test
  fun test(): Unit = runTest {
    val tracer = tracer {
      listener(EventLogger)
    }
    val response = llm(
      model = testModelFactory.openAi("gpt-4.1-nano"),
      messages = messages,
      responseType = Person::class,
      tracer = tracer,
    )
    verifyResponse(response)
  }

  private fun verifyResponse(response: List<ChatMessage>) {
    response.convert<Person>().shouldBe(Person(name = "Jeff Hudson", age = 29))
  }
}
