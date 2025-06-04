package osiris.agentic

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class InstructionsTest {
  @Test
  fun empty(): Unit = runTest {
    val instructions: Instructions =
      instructions {}
    instructions.create("The real instructions.").shouldBe(
      """
        The real instructions.
      """.trimIndent(),
    )
  }

  @Test
  fun typical(): Unit = runTest {
    val instructionBuilder: Instructions =
      instructions {
        add("Preamble.")
        add("Intro.")
      }
    instructionBuilder.create("The real instructions.").shouldBe(
      """
        Preamble.

        Intro.

        The real instructions.
      """.trimIndent(),
    )
  }
}
