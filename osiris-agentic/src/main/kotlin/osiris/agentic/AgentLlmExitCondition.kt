package osiris.agentic

import osiris.chat.LlmExitCondition
import osiris.chat.LlmState

internal class AgentLlmExitCondition : LlmExitCondition {
  private val delegate: LlmExitCondition = LlmExitCondition.Default()

  override fun shouldExit(state: LlmState): Boolean =
    delegate.shouldExit(state)
}
