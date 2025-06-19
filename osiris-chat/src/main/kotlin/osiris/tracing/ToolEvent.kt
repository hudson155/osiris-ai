package osiris.tracing

import dev.langchain4j.agent.tool.ToolExecutionRequest
import dev.langchain4j.data.message.ToolExecutionResultMessage
import osiris.core.Tool

/**
 * Each tool call will have a span.
 */
public object ToolEvent {
  public data class Start(
    val tool: Tool<*>,
    val executionRequest: ToolExecutionRequest,
  ) : Event.Details()

  public data class End(
    val executionResult: ToolExecutionResultMessage,
  ) : Event.Details()
}
