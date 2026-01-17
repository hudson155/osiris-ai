package osiris

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.UserMessage
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kairo.testing.postcondition
import kairo.testing.setup
import kairo.testing.test
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(AgentTest::class)
internal class PersonIdentifierTest {
  @Test
  fun test(
    context: Context,
    personIdentifier: PersonIdentifier,
  ): Unit =
    runTest {
      with(context) {
        setup {
          history.append(
            UserMessage("Jeff Hudson, 29, is a software engineer. He's also a pilot and an ultra trail runner.")
          )
        }
        test {
          personIdentifier.execute()
        }
        postcondition {
          history.lastOrNull().shouldBeInstanceOf<AiMessage>().text()
            .let { json.deserialize<PersonIdentifier.Output>(it) }
            .shouldBe(PersonIdentifier.Output("Jeff Hudson", 29))
        }
      }
    }
}
