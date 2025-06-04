package osiris.agentic

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldMatchEach
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class PersistSystemMessagesTest {
  @Test
  fun `persistSystemMessages = false (default)`(): Unit = runTest {
    val events = test()
    val execution = events.getExecution()
    withClue("Messages: ${execution.messages}.") {
      execution.messages.shouldMatchEach(
        { message ->
          message.shouldBe(UserMessage("What's 2+2?"))
        },
        { message ->
          message.shouldBe(AiMessage("4"))
        },
      )
    }
  }

  @Test
  fun `persistSystemMessages = true`(): Unit = runTest {
    val events = test {
      settings {
        persistSystemMessages = true
      }
    }
    val execution = events.getExecution()
    withClue("Messages: ${execution.messages}.") {
      execution.messages.shouldMatchEach(
        { message ->
          message.shouldBe(UserMessage("What's 2+2?"))
        },
        { message ->
          message.shouldBe(SystemMessage("Do the math. Return only the answer (nothing else)."))
        },
        { message ->
          message.shouldBe(AiMessage("4"))
        },
      )
    }
  }

  private suspend fun test(block: NetworkBuilder.() -> Unit = {}): List<Event> {
    val network: Network =
      network {
        entrypoint = mathAgent.name
        agents += mathAgent
        block()
      }
    return network.run(
      messages = listOf(
        UserMessage("What's 2+2?"),
      ),
    ).toList()
  }
}
