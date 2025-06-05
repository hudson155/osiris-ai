package osiris.agentic

import dev.langchain4j.data.message.UserMessage
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.onEach
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

  @Test
  fun test(): Unit = runTest {
    val messages = listOf(
      UserMessage("Jeff Hudson, 29, is a software engineer. He's also a pilot and an ultra trail runner."),
    )
    val flow = network.run(messages)
    val response = flow.onEach(::logEvent).getResponse()
    response.convert<Person>().shouldBe(Person(name = "Jeff Hudson", age = 29))
  }
}
