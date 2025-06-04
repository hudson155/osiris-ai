package osiris.agentic

import dev.langchain4j.data.message.UserMessage
import io.kotest.matchers.collections.shouldMatchEach
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.core.convert
import osiris.openAi.openAi

internal class SimpleTest {
  private val agent: Agent =
    agent("agent") {
      model = testModelFactory.openAi("gpt-4.1-nano") {
        temperature(0.20)
      }
      instructions = "Do the math. Return only the answer (nothing else)."
    }

  private val network: Network =
    network {
      entrypoint = "agent"
      agents += agent
    }

  @Test
  fun simple(): Unit = runTest {
    val response = test().getResponse()
    response.convert<String>().shouldBe("4")
  }

  @Test
  fun detailed(): Unit = runTest {
    val events = test().toList()
    events.shouldMatchEach(
      { event ->
        event.shouldBe(NetworkEvent.Start)
      },
      { event ->
        event.shouldBe(NetworkEvent.AgentStart("agent"))
      },
      { event ->
        event.shouldBe(NetworkEvent.AgentEnd("agent"))
      },
      { event ->
        event.shouldBeInstanceOf<NetworkEvent.End>()
        event.response.convert<String>().shouldBe("4")
      },
    )
  }

  private fun test(): Flow<NetworkEvent> =
    network.run(
      messages = listOf(
        UserMessage("What's 2+2?")
      ),
    )
}
