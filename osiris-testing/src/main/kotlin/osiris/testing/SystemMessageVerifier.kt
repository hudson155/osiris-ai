package osiris.testing

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.SystemMessage
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class SystemMessageVerifier(
  private val exactly: String,
) : MessageVerifier.Single() {
  override fun verify(message: ChatMessage) {
    message.shouldBeInstanceOf<SystemMessage>()
    message.text().shouldBe(exactly)
  }
}

public fun MutableList<MessageVerifier>.verifySystemMessage(exactly: String) {
  val verifier = SystemMessageVerifier(exactly)
  add(verifier)
}
