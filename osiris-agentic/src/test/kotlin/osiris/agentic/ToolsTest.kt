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
import osiris.evaluator.evaluate
import osiris.openAi.openAi

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ToolsTest {
  private val network: Network =
    network {
      entrypoint = weatherAgent.name
      agents += weatherAgent
    }

  private val events: LazySupplier<List<Event>> =
    LazySupplier {
      network.run(
        messages = listOf(
          UserMessage("What's the weather in Calgary and Edmonton?"),
        ),
      ).toList()
    }

  @Test
  fun response(): Unit = runTest {
    val response = events.get().getResponse()
    evaluate(
      model = testModelFactory.openAi("o3-mini"),
      response = response.convert<String>(),
      criteria = """
        Should say the weather in Calgary is 15 degrees Celsius and sunny,
        and that the weather in Edmonton is -30 degrees Celsius and snowing.
      """.trimIndent(),
    )
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
          event.shouldBe(Event.AgentStart(weatherAgent.name))
        },
        { event ->
          event.shouldBe(Event.AgentEnd(weatherAgent.name))
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
          message.shouldBe(UserMessage("What's the weather in Calgary and Edmonton?"))
        },
        { message ->
          message.shouldBeInstanceOf<AiMessage>()
          message.hasToolExecutionRequests().shouldBeFalse()
        },
      )
    }
  }
}
