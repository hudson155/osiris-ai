package osiris.prompt

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class InstructionsTest {
  @Test
  fun empty() {
    runTest {
      val instructionsBuilder: InstructionsBuilder =
        instructionsBuilder(includeDefaultInstructions = false) {}
      instructionsBuilder.build { "The real instructions." }.get().shouldBe(
        """
          The real instructions.
        """.trimIndent(),
      )
    }
  }

  @Test
  fun typical() {
    runTest {
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
  }

  @Test
  fun `default instructions`() {
    runTest {
      val instructionsBuilder: InstructionsBuilder =
        instructionsBuilder(includeDefaultInstructions = true)
      instructionsBuilder.build { "The real instructions." }.get().shouldBe(
        """
          # The system
          
          You're a part of a multi-agent system. You can consult other agents or hand off control to other agents.
          
          ## Consulting other agents
          
          Consulting other agents means that you ask them a question and they respond with a response; the other agent is a tool call.
          
          - When consulting other agents, succinctly tell them what to do or what you need. Don't tell them how to do their job.
          - Avoid rephrasing the user's question too much, since other agents might have more context to interpret it better than you can.
          - Each consultation should only request a single thing. For multiple things (such as a total value and a breakdown), make multiple consultations.
          - If you support parallel tool calls, you can consult multiple other agents at once.
          
          ## Handing off control to other agents
          
          Handing off control to other agents means that they assume control over this conversation, and you relinquish it.
          
          - Even if you support parallel tool calls, you can only hand off control to one agent.
          
          The real instructions.
        """.trimIndent(),
      )
    }
  }
}
