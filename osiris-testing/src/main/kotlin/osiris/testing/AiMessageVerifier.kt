package osiris.testing

import com.fasterxml.jackson.module.kotlin.readValue
import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import io.kotest.assertions.withClue
import io.kotest.inspectors.shouldForOne
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import osiris.core.osirisMapper

internal typealias ToolCallVerifier = (input: String) -> Unit

internal class AiMessageVerifier(
  private val response: Boolean,
  private val exactly: String?,
  private val toolCalls: List<Pair<String, ToolCallVerifier>>,
) : MessageVerifier.Single() {
  override fun verify(message: ChatMessage) {
    message.shouldBeInstanceOf<AiMessage>()
    with(message.text()) {
      if (response || exactly != null) shouldNotBeNull() else shouldBeNull()
    }
    with(message.hasToolExecutionRequests()) {
      if (toolCalls.isNotEmpty()) shouldBeTrue() else shouldBeFalse()
    }
    if (exactly != null) {
      message.text().shouldBe(exactly)
    }
    if (toolCalls.isNotEmpty()) {
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
}

public class AiMessageVerifierBuilder internal constructor() {
  public var response: Boolean = false
  public var exactly: String? = null
  public val toolCalls: MutableList<Pair<String, ToolCallVerifier>> = mutableListOf()

  internal fun build(): AiMessageVerifier =
    AiMessageVerifier(
      response = response,
      exactly = exactly,
      toolCalls = toolCalls,
    )
}

public fun MutableList<MessageVerifier>.verifyAiMessage(block: AiMessageVerifierBuilder.() -> Unit = {}) {
  val verifier = AiMessageVerifierBuilder().apply(block).build()
  add(verifier)
}

public inline fun <reified Input : Any> AiMessageVerifierBuilder.toolCall(name: String, input: Input) {
  toolCalls += Pair(name) { osirisMapper.readValue<Input>(it).shouldBe(input) }
}
