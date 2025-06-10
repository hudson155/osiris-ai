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

internal class StructuredOutputTest {
  private val messages: List<ChatMessage> =
    listOf(
      UserMessage("Jeff Hudson, 29, is a software engineer. He's also a pilot and an ultra trail runner."),
      SystemMessage("Provide a JSON representation of the person matching this description."),
    )

  @Test
  fun test(): Unit = runTest {
    val (response, trace) = llm(
      model = testModelFactory.openAi("gpt-4.1-nano"),
      responseType = Person::class,
      messages = messages,
    )
    verifyResponse(response)
    verifyTrace(trace)
  }

  private fun verifyResponse(response: List<ChatMessage>) {
    response.convert<Person>().shouldBe(Person(name = "Jeff Hudson", age = 29))
  }

  private fun verifyTrace(trace: List<Span<*>>) {
    trace.shouldMatchEach(
      { it.details.shouldBeInstanceOf<ChatEvent>() },
    )
  }
}
