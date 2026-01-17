package osiris

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.UserMessage
import io.kotest.matchers.shouldBe
import kairo.testing.postcondition
import kairo.testing.test
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(AgentTest::class)
internal class MathAgentTest {
  @Test
  fun test(
    mathAgent: MathAgent,
    modelFactory: ModelFactory,
  ): Unit =
    runTest {
      val context = context {
        defaultModel = modelFactory.openAi("gpt-5.2")
        history.append(UserMessage("What's 2+2?"))
      }
      with(context) {
        test {
          mathAgent.execute()
        }
        postcondition {
          val aiMessage = history.lastOrNull() as AiMessage?
          aiMessage?.text().shouldBe("4")
        }
      }
    }
}
