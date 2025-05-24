package osiris.ennead

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class HandoffTest {
  private data class State(
    val name: String,
    val result: List<String>,
    val logs: List<String>, // Just for testing.
  ) {
    constructor(name: String) : this(
      name = name,
      result = emptyList(),
      logs = emptyList(),
    )
  }

  private val greetingAgent: Agent<State> =
    agent("Greeting") {
      custom {
        state = state.copy(
          result = state.result + "Hi ${state.name}!",
          logs = state.logs + "Greeting start",
        )
        handoff("Pleasantry")
        state = state.copy(logs = state.logs + "Greeting end")
      }
    }

  private val pleasantryAgent: Agent<State> =
    agent("Pleasantry") {
      custom {
        state = state.copy(
          result = state.result + "I hope you're doing well today.",
          logs = state.logs + "Pleasantry start",
        )
        handoff("Question")
        state = state.copy(logs = state.logs + "Pleasantry end")
      }
    }

  private val questionAgent: Agent<State> =
    agent("Question") {
      custom {
        state = state.copy(
          result = state.result + "Can you help me lift this heavy object?",
          logs = state.logs + "Question start"
        )
        state = state.copy(logs = state.logs + "Question end")
      }
    }

  private val runner: AgentRunner<State> =
    runner {
      agent(greetingAgent)
      agent(pleasantryAgent)
      agent(questionAgent)
    }

  @Test
  fun `starts with Greeting agent`() {
    val result = runner.run(initialState = State(name = "Jeff"), initialAgentName = "Greeting")
    val expected = State(
      name = "Jeff",
      result = listOf("Hi Jeff!", "I hope you're doing well today.", "Can you help me lift this heavy object?"),
      logs = listOf(
        "Greeting start",
        "Greeting end",
        "Pleasantry start",
        "Pleasantry end",
        "Question start",
        "Question end",
      ),
    )
    result.shouldBe(expected)
  }

  @Test
  fun `starts with Pleasantry agent`() {
    val result = runner.run(initialState = State(name = "Jeff"), initialAgentName = "Pleasantry")
    val expected = State(
      name = "Jeff",
      result = listOf("I hope you're doing well today.", "Can you help me lift this heavy object?"),
      logs = listOf(
        "Pleasantry start",
        "Pleasantry end",
        "Question start",
        "Question end",
      ),
    )
    result.shouldBe(expected)
  }

  @Test
  fun `starts with Question agent`() {
    val result = runner.run(initialState = State(name = "Jeff"), initialAgentName = "Question")
    val expected = State(
      name = "Jeff",
      result = listOf("Can you help me lift this heavy object?"),
      logs = listOf(
        "Question start",
        "Question end",
      ),
    )
    result.shouldBe(expected)
  }
}
