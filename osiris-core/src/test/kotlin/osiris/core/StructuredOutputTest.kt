package osiris.core

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import io.kotest.inspectors.shouldForExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.event.Event
import osiris.event.messages
import osiris.openAi.openAi
import osiris.tracing.ChatEvent
import osiris.tracing.Trace
import osiris.tracing.trace

internal class StructuredOutputTest {
  private val messages: List<ChatMessage> =
    listOf(
      UserMessage("Jeff Hudson, 29, is a software engineer. He's also a pilot and an ultra trail runner."),
      SystemMessage("Provide a JSON representation of the person matching this description."),
    )

  @Test
  fun test(): Unit = runTest {
    val (response, trace) = trace {
      llm(
        model = testModelFactory.openAi("gpt-4.1-nano"),
        messages = messages,
        responseType = Person::class,
      )
    }
    verifyResponse(response)
    verifyTrace(trace)
  }

  private suspend fun verifyResponse(response: Flow<Event>) {
    response.messages.last().convert<Person>().shouldBe(Person(name = "Jeff Hudson", age = 29))
  }

  private fun verifyTrace(trace: Trace) {
    with(trace.spans.map { it.details }) {
      shouldForExactly(1) { it.shouldBeInstanceOf<ChatEvent>() }
      shouldHaveSize(1)
    }
  }
}
