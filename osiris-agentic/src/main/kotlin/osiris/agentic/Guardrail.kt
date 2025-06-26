package osiris.agentic

import dev.langchain4j.data.message.ChatMessage

public class Guardrail(
  private val agent: Agent,
  private val validate: (List<ChatMessage>) -> Unit,
) {
  public suspend fun execute() {
    val executionContext = getExecutionContext()
    agent.execute()
    validate(executionContext.response)
  }
}
