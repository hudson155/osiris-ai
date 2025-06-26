package osiris.agentic

import dev.langchain4j.data.message.ChatMessage

/**
 * Input guardrails asynchronously validate the agent's input, possibly throwing an exception.
 */
public class Guardrail(
  private val agentName: String,
  private val validate: (messages: List<ChatMessage>) -> Unit,
) {
  public suspend fun execute() {
    val executionContext = getExecutionContext()
    val agent = executionContext.getAgent(agentName)
    agent.execute()
    validate(executionContext.response)
  }
}
