package osiris.testing

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.ToolExecutionResultMessage
import dev.langchain4j.data.message.UserMessage
import io.kotest.assertions.fail
import io.kotest.assertions.withClue
import io.kotest.inspectors.shouldForOne
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

private typealias MessageVerifier = (messageIterator: Iterator<ChatMessage>) -> Unit

public class MessagesVerifier {
  public val verifiers: MutableList<MessageVerifier> = mutableListOf()
}

public fun verifyMessages(messages: List<ChatMessage>, block: MessagesVerifier.() -> Unit) {
  val verifier = MessagesVerifier()
  verifier.block()
  val messageIterator = messages.iterator()
  verifier.verifiers.forEach { it(messageIterator) }
  if (messageIterator.hasNext()) {
    fail(
      buildList {
        add("Expected ${verifier.verifiers.size} messages, but got ${messages.size}.")
        add("All previous messages matched.")
        add("The following messages were not verified:")
        addAll(messages.subList(verifier.verifiers.size, messages.size))
      }.joinToString(" "),
    )
  }
}

public fun MessagesVerifier.verifySystemMessage(exactly: String) {
  verifiers += verifySingleMessage<SystemMessage> { message ->
    message.text().shouldBe(exactly)
  }
}

public fun MessagesVerifier.verifyUserMessage(exactly: String) {
  verifiers += verifySingleMessage<UserMessage> { message ->
    message.singleText().shouldBe(exactly)
  }
}

public fun MessagesVerifier.verifyAiMessage(exactly: String) {
  verifiers += verifySingleMessage<AiMessage> { message ->
    message.text().shouldNotBeNull()
    message.hasToolExecutionRequests().shouldBeFalse()
    message.text().shouldBe(exactly)
  }
}

public fun MessagesVerifier.verifyAiMessage(block: ToolCallReceiver.() -> Unit) {
  val receiver = ToolCallReceiver()
  receiver.block()
  verifiers += verifySingleMessage<AiMessage> { message ->
    message.text().shouldBeNull()
    message.hasToolExecutionRequests().shouldBeTrue()
    message.toolExecutionRequests().forEach { execution ->
      withClue("Tool execution request $execution was not expected.") {
        receiver.toolCalls.shouldForOne { (name, input) ->
          execution.id().shouldNotBeNull() // Not verified.
          execution.name().shouldBe(name)
          input(execution.arguments())
        }
      }
    }
  }
}

public fun MessagesVerifier.verifyToolMessages(block: ToolExecutionReceiver.() -> Unit) {
  val receiver = ToolExecutionReceiver()
  receiver.block()
  verifiers += { messageIterator ->
    repeat(receiver.executions.size) {
      verifySingleMessage<ToolExecutionResultMessage> { message ->
        receiver.executions.shouldForOne { (name, output) ->
          message.id().shouldNotBeNull() // Not verified.
          message.toolName().shouldBe(name)
          output(message.text())
        }
      }(messageIterator)
    }
  }
}

private inline fun <reified T : ChatMessage> verifySingleMessage(
  crossinline block: (message: T) -> Unit,
): MessageVerifier =
  { messageIterator ->
    withClue("Expected another message, but there was none.") {
      messageIterator.hasNext().shouldBeTrue()
    }
    val message = messageIterator.next()
    message.shouldBeInstanceOf<T>()
    block(message)
  }
