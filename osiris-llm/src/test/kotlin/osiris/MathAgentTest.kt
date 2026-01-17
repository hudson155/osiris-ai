package osiris

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.UserMessage
import io.kotest.matchers.shouldBe
import kairo.dependencyInjection.KoinExtension
import kairo.testing.postcondition
import kairo.testing.test
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.koin.core.KoinApplication
import org.koin.dsl.module
import org.koin.ksp.generated.module

@ExtendWith(KoinExtension::class)
internal class MathAgentTest {
  @BeforeEach
  fun beforeEach(koin: KoinApplication) {
    koin.modules(
      TestModule.module,
      module {
        single { ModelFactory() }
      }
    )
  }

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
          val aiMessage = getAiMessage()
          aiMessage.text().shouldBe("4")
        }
      }
    }
}

public suspend fun Context.getAiMessage(): AiMessage =
  (history.get().last() as AiMessage)
