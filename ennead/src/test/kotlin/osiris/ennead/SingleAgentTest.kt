package osiris.ennead

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class SingleAgentTest {
  private val agent: Agent<String?> =
    agent("Trivial") {
      custom {
        state = "I'm a trivial agent!"
      }
    }

  private val runner: AgentRunner<String?> =
    runner {
      agent(agent)
    }

  @Test
  fun test(): Unit = runTest {
    val result = runner.run(initialState = null, initialAgentName = "Trivial")
    result.shouldBe("I'm a trivial agent!")
  }
}
