package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import kotlinx.coroutines.withContext

/**
 * Input guardrails asynchronously validate the agent's input, possibly throwing an exception.
 */
public class Guardrail(
  public val agentName: String,
  private val validate: (messages: List<ChatMessage>) -> Unit,
) {
  public abstract class Exception(
    public val text: String,
  ) : kotlin.Exception() {
    public abstract val agentName: String
  }

  public suspend fun execute() {
    val outerExecutionContext = getExecutionContext()
    val innerExecutionContext = outerExecutionContext.inner(agentName = agentName)
    withContext(innerExecutionContext) {
      innerExecutionContext.execute()
      validate(innerExecutionContext.state.get().messages)
    }
  }
}
