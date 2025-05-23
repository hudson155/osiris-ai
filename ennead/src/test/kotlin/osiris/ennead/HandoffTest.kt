package osiris.ennead

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class HandoffTest {
  private data class State(
    val name: String,
    val result: List<String>,
  ) {
    constructor(name: String) : this(
      name = name,
      result = emptyList(),
    )
  }

  private val greetingAgent: Agent<State> =
    agent("Greeting") {
      custom {
        state = state.copy(result = state.result + "Hi ${state.name}!")
        handoff("Pleasantry")
      }
    }

  private val pleasantryAgent: Agent<State> =
    agent("Pleasantry") {
      custom {
        state = state.copy(result = state.result + "I hope you're doing well today.")
      }
    }

  private val runner: AgentRunner<State> =
    runner {
      agent(greetingAgent)
      agent(pleasantryAgent)
    }

  @Test
  fun `starts with Greeting agent`() {
    val result = runner.run(initialState = State(name = "Jeff"), initialAgentName = "Greeting")
    result.shouldBe(
      State(name = "Jeff", result = listOf("Hi Jeff!", "I hope you're doing well today.")),
    )
  }

  @Test
  fun `starts with Pleasantry agent`() {
    val result = runner.run(initialState = State(name = "Jeff"), initialAgentName = "Pleasantry")
    result.shouldBe(
      State(name = "Jeff", result = listOf("I hope you're doing well today.")),
    )
  }
}
