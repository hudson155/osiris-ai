package osiris.agentic

import dev.langchain4j.data.message.UserMessage
import io.kotest.inspectors.shouldForNone
import io.kotest.inspectors.shouldForSome
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class IncludeConsultationProgressUpdatesTest {
  @Test
  fun `includeConsultationProgressUpdates = true (default)`(): Unit = runTest {
    val events = test()
    events.shouldForSome { event ->
      event.shouldBeInstanceOf<Event.ProgressUpdate>()
    }
  }

  @Test
  fun `includeConsultationProgressUpdates = false`(): Unit = runTest {
    val events = test {
      settings {
        includeConsultationProgressUpdates = false
      }
    }
    events.shouldForNone { event ->
      event.shouldBeInstanceOf<Event.ProgressUpdate>()
    }
  }

  private suspend fun test(block: NetworkBuilder.() -> Unit = {}): List<Event> {
    val network: Network =
      network {
        entrypoint = ecommerceChatbot.name
        agents += ecommerceChatbot
        agents += ecommerceOrderTracker
        block()
      }
    return network.run(
      messages = listOf(
        UserMessage("Where is my order? The ID is ord_0."),
      ),
    ).toList()
  }
}
