package osiris.agentic

import dev.langchain4j.data.message.ChatMessage

public class Guardrail(
  private val agentName: String,
  private val validate: (List<ChatMessage>) -> Unit,
) {
  public suspend fun execute() {
    val executionContext = getExecutionContext()
    val agent = executionContext.getAgent(agentName)
    agent.execute()
    validate(executionContext.response)
  }
}
