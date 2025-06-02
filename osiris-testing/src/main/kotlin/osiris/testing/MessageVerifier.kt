package osiris.testing

import dev.langchain4j.data.message.ChatMessage

internal abstract class MessageVerifier {
  abstract val count: Int

  abstract fun verify(messages: List<ChatMessage>)

  internal abstract class Single : MessageVerifier() {
    final override val count = 1

    final override fun verify(messages: List<ChatMessage>) {
      verify(messages.single())
    }

    abstract fun verify(message: ChatMessage)
  }

  internal abstract class Multiple(final override val count: Int) : MessageVerifier() {
    abstract override fun verify(messages: List<ChatMessage>)
  }
}
