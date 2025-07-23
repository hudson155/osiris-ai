package osiris.agentic

import osiris.chat.LlmExitCondition
import osiris.chat.LlmState

internal class AgentLlmExitCondition : LlmExitCondition {
  private val delegate: LlmExitCondition = LlmExitCondition.Default()

  override suspend fun shouldExit(state: LlmState): Boolean {
    val executionContext = getExecutionContext()
    if (executionContext.state.get().llmExit) return true
    return delegate.shouldExit(state)
  }
}
