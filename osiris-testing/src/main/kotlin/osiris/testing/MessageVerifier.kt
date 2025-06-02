package osiris.testing

import dev.langchain4j.data.message.ChatMessage
import io.kotest.assertions.fail
import io.kotest.assertions.withClue
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual

public abstract class MessageVerifier {
  public abstract val count: Int

  public abstract fun verify(messages: List<ChatMessage>)

  public abstract class Single : MessageVerifier() {
    final override val count: Int = 1

    final override fun verify(messages: List<ChatMessage>) {
      verify(messages.single())
    }

    public abstract fun verify(message: ChatMessage)
  }

  public abstract class Multiple(final override val count: Int) : MessageVerifier() {
    abstract override fun verify(messages: List<ChatMessage>)
  }
}

public fun verifyMessages(messages: List<ChatMessage>, block: MutableList<MessageVerifier>.() -> Unit) {
  val verifiers = buildList(block)
  var remainingMessages = messages
  verifiers.forEach { verifier ->
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
