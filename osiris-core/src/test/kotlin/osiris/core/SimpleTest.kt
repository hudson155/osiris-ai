package osiris.core

import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.openAi.openAi
import osiris.testing.verifyMessages
import osiris.testing.verifyResponse

internal class SimpleTest {
  @Test
  fun test(): Unit = runTest {
    val osirisEvents = osiris(
      model = modelFactory.openAi("gpt-4.1-nano"),
      messages = listOf(
        UserMessage("What's 2+2?"),
        SystemMessage("Do the math. Return only the answer (nothing else)."),
      ),
    ).toList()
    verifyMessages(osirisEvents.getMessages()) {
      verifyResponse()
    }
    osirisEvents.getMessages().getResponseAs<String>().shouldBe("4")
  }
}
