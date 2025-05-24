package osiris.ennead

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class ConsultTest {
  private data class State(
    val inputs: List<Int>,
    val sum: Int?,
    val result: String?,
    val logs: List<String>, // Just for testing.
  ) {
    constructor(inputs: List<Int>) : this(
      inputs = inputs,
      sum = null,
      result = null,
      logs = emptyList(),
    )
  }

  private val exampleAgent: Agent<State> =
    agent("Example") {
      custom {
        state = state.copy(
          logs = state.logs + "Example start",
        )
        if (state.sum == null) {
          consult("Math")
        }
        state = state.copy(
          result = state.sum?.let { "The sum is $it." } ?: state.result,
          logs = state.logs + "Example end",
        )
      }
    }

  private val mathAgent: Agent<State> =
    agent("Math") {
      custom {
        state = state.copy(
          logs = state.logs + "Math start",
        )
        if (state.sum == null) {
          consult("Sum")
        }
        state = state.copy(
          logs = state.logs + "Math end",
        )
      }
    }

  private val sumAgent: Agent<State> =
    agent("Sum") {
      custom {
        state = state.copy(
          logs = state.logs + "Sum start",
        )
        state = state.copy(
          sum = state.inputs.sum(),
          logs = state.logs + "Sum end",
        )
      }
    }

  private val runner: AgentRunner<State> =
    runner {
      agent(exampleAgent)
      agent(mathAgent)
      agent(sumAgent)
    }

  @Test
  fun `starts with Example agent`() {
    val result = runner.run(initialState = State(inputs = listOf(1, 2, 3)), initialAgentName = "Example")
    val expected = State(
      inputs = listOf(1, 2, 3),
      sum = 6,
      result = "The sum is 6.",
      logs = listOf(
        "Example start",
        "Example end",
        "Math start",
        "Math end",
        "Sum start",
        "Sum end",
        "Math start",
        "Math end",
        "Example start",
        "Example end",
      ),
    )
    result.shouldBe(expected)
  }

  @Test
  fun `starts with Math agent`() {
    val result = runner.run(initialState = State(inputs = listOf(1, 2, 3)), initialAgentName = "Math")
    val expected = State(
      inputs = listOf(1, 2, 3),
      sum = 6,
      result = null,
      logs = listOf(
        "Math start",
        "Math end",
        "Sum start",
        "Sum end",
        "Math start",
        "Math end",
      ),
    )
    result.shouldBe(expected)
  }

  @Test
  fun `starts with Sum agent`() {
    val result = runner.run(initialState = State(inputs = listOf(1, 2, 3)), initialAgentName = "Sum")
    val expected = State(
      inputs = listOf(1, 2, 3),
      sum = 6,
      result = null,
      logs = listOf(
        "Sum start",
        "Sum end",
      ),
    )
    result.shouldBe(expected)
  }
}
