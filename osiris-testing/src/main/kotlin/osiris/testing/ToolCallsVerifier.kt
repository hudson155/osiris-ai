package osiris.testing

import com.fasterxml.jackson.module.kotlin.readValue
import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import io.kotest.assertions.withClue
import io.kotest.inspectors.shouldForOne
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import osiris.core.osirisMapper

internal typealias ToolCallVerifier = (input: String) -> Unit

internal class ToolCallsVerifier(
  private val toolCalls: List<Pair<String, ToolCallVerifier>>,
) : MessageVerifier.Single() {
  override fun verify(message: ChatMessage) {
    message.shouldBeInstanceOf<AiMessage>()
    message.text().shouldBeNull()
    message.hasToolExecutionRequests().shouldBeTrue()
    message.toolExecutionRequests().forEach { execution ->
      withClue("Tool execution request $execution was not expected.") {
        toolCalls.shouldForOne { (name, input) ->
          execution.id().shouldNotBeNull() // Not verified.
          execution.name().shouldBe(name)
          input(execution.arguments())
        }
      }
    }
  }
}

public class ToolCallsVerifierBuilder internal constructor() {
  public val toolCalls: MutableList<Pair<String, ToolCallVerifier>> = mutableListOf()

  internal fun build(): ToolCallsVerifier =
    ToolCallsVerifier(
      toolCalls = toolCalls,
    )
}

public fun MutableList<MessageVerifier>.verifyToolCalls(block: ToolCallsVerifierBuilder.() -> Unit) {
  val verifier = ToolCallsVerifierBuilder().apply(block).build()
  add(verifier)
}

public inline fun <reified Input : Any> ToolCallsVerifierBuilder.toolCall(name: String, input: Input) {
  toolCalls += Pair(name) { osirisMapper.readValue<Input>(it).shouldBe(input) }
}
