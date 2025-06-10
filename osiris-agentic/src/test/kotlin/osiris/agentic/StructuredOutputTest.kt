package osiris.agentic

import dev.langchain4j.data.message.UserMessage
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import osiris.core.convert
import osiris.core.response

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class StructuredOutputTest {
  private val network: Network =
    network("network") {
      entrypoint = personCreator.name
      agents += personCreator
    }

  @Test
  fun test(): Unit = runTest {
    val messages = listOf(
      UserMessage("Jeff Hudson, 29, is a software engineer. He's also a pilot and an ultra trail runner."),
    )
    val response = network.run(messages).onEach(::logEvent).response().last()
    response.convert<Person>().shouldBe(Person(name = "Jeff Hudson", age = 29))
  }
}
