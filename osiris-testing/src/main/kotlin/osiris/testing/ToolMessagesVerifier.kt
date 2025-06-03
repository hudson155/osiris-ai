package osiris.testing

import com.fasterxml.jackson.module.kotlin.readValue
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.ToolExecutionResultMessage
import io.kotest.inspectors.shouldForOne
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import osiris.core.osirisMapper

internal typealias ToolExecutionVerifier = (output: String) -> Unit

internal class ToolMessagesVerifier(
  private val executions: List<Pair<String, ToolExecutionVerifier>>,
) : MessageVerifier.Multiple(executions.size) {
  override fun verify(messages: List<ChatMessage>) {
    messages.forEach { message ->
      message.shouldBeInstanceOf<ToolExecutionResultMessage>()
      executions.shouldForOne { (name, output) ->
        message.id().shouldNotBeNull() // Not verified.
        message.toolName().shouldBe(name)
        output(message.text())
      }
    }
  }
}

public class ToolMessagesVerifierBuilder internal constructor() {
  public val executions: MutableList<Pair<String, ToolExecutionVerifier>> = mutableListOf()

  internal fun build(): ToolMessagesVerifier =
    ToolMessagesVerifier(
      executions = executions,
    )
}

public fun MutableList<MessageVerifier>.verifyToolMessages(block: ToolMessagesVerifierBuilder.() -> Unit) {
  val verifier = ToolMessagesVerifierBuilder().apply(block).build()
  add(verifier)
}

public inline fun <reified Output : Any> ToolMessagesVerifierBuilder.toolMessage(name: String, output: Output) {
  executions += Pair(name) { osirisMapper.readValue<Output>(it).shouldBe(output) }
}
