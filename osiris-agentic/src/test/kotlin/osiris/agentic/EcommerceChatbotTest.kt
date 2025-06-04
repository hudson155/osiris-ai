package osiris.agentic

import dev.langchain4j.data.message.UserMessage
import io.kotest.inspectors.shouldForSome
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
internal class EcommerceChatbotTest {
  private val network: Network =
    network {
      entrypoint = ecommerceChatbot.name
      agents += ecommerceChatbot
      agents += ecommerceOrderTracker
    }

  private val events: LazySupplier<List<Event>> =
    LazySupplier {
      network.run(
        messages = listOf(
          UserMessage("Where are my orders? The IDs are ord_0 and ord_1."),
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
        Should say that ord_0 has not been shipped yet,
        and that ord_1 is in transit.
      """.trimIndent(),
    )
  }

  @Test
  fun events(): Unit = runTest {
    val events = events.get()
    events.shouldForSome { event ->
      event.shouldBeInstanceOf<Event.ProgressUpdate>()
    }
  }
}
