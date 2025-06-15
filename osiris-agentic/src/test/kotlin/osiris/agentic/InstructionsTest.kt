package osiris.agentic

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class InstructionsTest {
  @Test
  fun empty(): Unit = runTest {
    val instructionsBuilder: InstructionsBuilder =
      instructionsBuilder(includeDefaultInstructions = false) {}
    instructionsBuilder.build { "The real instructions." }.get().shouldBe(
      """
        The real instructions.
      """.trimIndent(),
    )
  }

  @Test
  fun typical(): Unit = runTest {
    val instructionsBuilder: InstructionsBuilder =
      instructionsBuilder(includeDefaultInstructions = false) {
        add { "First" }
        add { "Second" }
      }
    instructionsBuilder.build { "Third" }.get().shouldBe(
      """
        First
        
        Second
        
        Third
      """.trimIndent(),
    )
  }

  @Test
  fun `default instructions`(): Unit = runTest {
    val instructionsBuilder: InstructionsBuilder =
      instructionsBuilder(includeDefaultInstructions = true)
    instructionsBuilder.build { "The real instructions." }.get().shouldBe(
      """
        # The system
        
        You're a part of a multi-agent system.
        You can consult other agents.
        When consulting other agents, succinctly tell them what to do or what you need.
        Don't tell them how to do their job.
        
        The real instructions.
      """.trimIndent(),
    )
  }
}
