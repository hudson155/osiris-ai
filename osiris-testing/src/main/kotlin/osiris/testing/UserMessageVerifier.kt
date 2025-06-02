package osiris.testing

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.UserMessage
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class UserMessageVerifier(private val exactly: String) : MessageVerifier.Single() {
  override fun verify(message: ChatMessage) {
    message.shouldBeInstanceOf<UserMessage>()
    message.singleText().shouldBe(exactly)
  }
}

public fun MessagesVerifier.verifyUserMessage(exactly: String) {
  verifiers += UserMessageVerifier(exactly)
}
