package osiris.agentic

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.UserMessage
import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.collections.shouldMatchEach
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kairo.lazySupplier.LazySupplier
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import osiris.core.convert

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class StructuredOutputTest {
  private val network: Network =
    network {
      entrypoint = personCreator.name
      agents += personCreator
    }

  private val events: LazySupplier<List<Event>> =
    LazySupplier {
      network.run(
        messages = listOf(
          UserMessage("Jeff Hudson, 29, is a software engineer. He's also a pilot and an ultra trail runner."),
        ),
      ).toList()
    }

  @Test
  fun response(): Unit = runTest {
    val response = events.get().getResponse()
    response.convert<Person>().shouldBe(Person(name = "Jeff Hudson", age = 29))
  }

  @Test
  fun events(): Unit = runTest {
    val events = events.get()
    withClue("Events: $events.") {
      events.shouldMatchEach(
        { event ->
          event.shouldBeInstanceOf<Event.Start>()
        },
        { event ->
          event.shouldBe(Event.AgentStart(personCreator.name))
        },
        { event ->
          event.shouldBe(Event.AgentEnd(personCreator.name))
        },
        { event ->
          event.shouldBeInstanceOf<Event.End>()
        },
      )
    }
  }

  @Test
  fun execution(): Unit = runTest {
    val execution = events.get().getExecution()
    withClue("Messages: ${execution.messages}.") {
      execution.messages.shouldMatchEach(
        { message ->
          message.shouldBe(
            UserMessage("Jeff Hudson, 29, is a software engineer. He's also a pilot and an ultra trail runner."),
          )
        },
        { message ->
          message.shouldBeInstanceOf<AiMessage>()
          message.hasToolExecutionRequests().shouldBeFalse()
        },
      )
    }
  }
}
