package osiris.testing

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.types.shouldBeInstanceOf

internal class ResponseVerifier() : MessageVerifier.Single() {
  override fun verify(message: ChatMessage) {
    message.shouldBeInstanceOf<AiMessage>()
    message.text().shouldNotBeNull()
    message.hasToolExecutionRequests().shouldBeFalse()
  }
}

public fun MutableList<MessageVerifier>.verifyResponse() {
  val verifier = ResponseVerifier()
  add(verifier)
}
