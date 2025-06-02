package osiris.core

import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import kairo.environmentVariableSupplier.DefaultEnvironmentVariableSupplier
import kairo.protectedString.ProtectedString
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.openAi.openAi
import osiris.openAi.openAiApiKey
import osiris.testing.getMessages
import osiris.testing.verifyAiMessage
import osiris.testing.verifyMessages

internal class OsirisTest {
  @Suppress("UnnecessaryLet")
  @OptIn(ProtectedString.Access::class)
  private val modelFactory: ModelFactory =
    modelFactory {
      openAiApiKey = DefaultEnvironmentVariableSupplier["OPEN_AI_API_KEY"]?.let { ProtectedString(it) }
    }

  @Test
  fun test(): Unit = runTest {
    val response = osiris(
      model = modelFactory.openAi("gpt-4.1-nano"),
      messages = listOf(
        UserMessage("What's 2+2?"),
        SystemMessage("Do the math. Return only the answer (nothing else)."),
      ),
    )
    verifyMessages(response.getMessages()) {
      verifyAiMessage("4")
    }
  }
}
