package osiris.testing

import dev.langchain4j.data.message.ChatMessage
import io.kotest.assertions.fail
import io.kotest.assertions.withClue
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual

public class MessagesVerifier {
  internal val verifiers: MutableList<MessageVerifier> = mutableListOf()
}

public fun verifyMessages(messages: List<ChatMessage>, block: MessagesVerifier.() -> Unit) {
  val verifier = MessagesVerifier()
  verifier.block()
  var remainingMessages = messages
  verifier.verifiers.forEach { verifier ->
    withClue("Tried to verify more messages, but none were remaining.") {
      remainingMessages.size.shouldBeGreaterThanOrEqual(verifier.count)
    }
    verifier.verify(remainingMessages.take(verifier.count))
    remainingMessages = remainingMessages.drop(verifier.count)
  }
  if (remainingMessages.isNotEmpty()) {
    fail(
      buildList {
        add("All previous messages matched.")
        add("The following messages were not verified:")
        addAll(remainingMessages)
      }.joinToString(" "),
    )
  }
}
