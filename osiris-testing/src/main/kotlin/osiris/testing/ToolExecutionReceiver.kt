package osiris.testing

import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.matchers.shouldBe
import osiris.core.osirisMapper

internal typealias ToolExecutionVerifier = (output: String) -> Unit

public class ToolExecutionReceiver {
  public val executions: MutableList<Pair<String, ToolExecutionVerifier>> = mutableListOf()
}

public inline fun <reified Output : Any> ToolExecutionReceiver.execution(name: String, output: Output) {
  executions += Pair(name) { osirisMapper.readValue<Output>(it).shouldBe(output) }
}
