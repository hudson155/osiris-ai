package osiris.agentic

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.UserMessage
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.toList
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

  private val messages: List<UserMessage> =
    listOf(
      UserMessage("Jeff Hudson, 29, is a software engineer. He's also a pilot and an ultra trail runner."),
    )

  @Test
  fun test(): Unit = runTest {
    val events = network.run(messages).toList()
    verifyResponse(events.response())
  }

  private fun verifyResponse(response: AiMessage) {
    response.convert<Person>().shouldBe(Person(name = "Jeff Hudson", age = 29))
  }
}
