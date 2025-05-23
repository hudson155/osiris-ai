package osiris.ennead

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class MultiAgentTest {
  private val cowAgent: Agent<String?> =
    agent("Cow") {
      custom {
        state = "Moo!"
      }
    }

  private val duckAgent: Agent<String?> =
    agent("Duck") {
      custom {
        state = "Quack!"
      }
    }

  private val runner: AgentRunner<String?> =
    runner {
      agent(cowAgent)
      agent(duckAgent)
    }

  @Test
  fun `starts with Cow agent`() {
    val result = runner.run(initialState = null, initialAgentName = "Cow")
    result.shouldBe("Moo!")
  }

  @Test
  fun `starts with Duck agent`() {
    val result = runner.run(initialState = null, initialAgentName = "Duck")
    result.shouldBe("Quack!")
  }
}
