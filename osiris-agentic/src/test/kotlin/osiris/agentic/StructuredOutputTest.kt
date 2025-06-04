package osiris.agentic

import dev.langchain4j.data.message.UserMessage
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.matchers.shouldBe
import kairo.lazySupplier.LazySupplier
import kairo.serialization.util.kairoWrite
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import osiris.core.convert

private val logger: KLogger = KotlinLogging.logger {}

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
  fun trace(): Unit = runTest {
    val events = events.get()
    val trace = traceMapper.kairoWrite(events)
    logger.info { "Events: $trace" }
  }
}
