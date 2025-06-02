package osiris.testing

import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.matchers.shouldBe
import osiris.core.osirisMapper

internal typealias ToolCallVerifier = (input: String) -> Unit

public class ToolCallReceiver {
  public val toolCalls: MutableList<Pair<String, ToolCallVerifier>> = mutableListOf()
}

public inline fun <reified Input : Any> ToolCallReceiver.toolCall(name: String, input: Input) {
  toolCalls += Pair(name) { osirisMapper.readValue<Input>(it).shouldBe(input) }
}
