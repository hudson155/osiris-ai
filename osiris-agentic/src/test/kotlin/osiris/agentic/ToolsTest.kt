package osiris.agentic

import dev.langchain4j.data.message.UserMessage
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kairo.lazySupplier.LazySupplier
import kairo.serialization.util.kairoWrite
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import osiris.core.convert
import osiris.evaluator.evaluate
import osiris.openAi.openAi

private val logger: KLogger = KotlinLogging.logger {}

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
        Should say that the weather in Calgary is 15 degrees Celsius and sunny,
        and that the weather in Edmonton is -30 degrees Celsius and snowing.
      """.trimIndent(),
    )
  }

  @Test
  fun trace(): Unit = runTest {
    val events = events.get()
    val trace = traceMapper.kairoWrite(events)
    logger.info { "Events: $trace" }
  }
}
