package osiris.tracing

import dev.langchain4j.agent.tool.ToolExecutionRequest
import dev.langchain4j.data.message.ToolExecutionResultMessage
import osiris.chat.Tool

/**
 * Each tool call will have a span.
 */
internal object ToolEvent {
  fun start(tool: Tool<*>, executionRequest: ToolExecutionRequest): Event.Start.Creator =
    Event.Start.Creator(
      type = "Tool",
      content = executionRequest.arguments(),
      properties = mapOf(
        "tool" to tool,
        "executionRequest" to executionRequest,
      ),
    )

  fun end(executionResult: ToolExecutionResultMessage): Event.End.Creator =
    Event.End.Creator(
      content = executionResult.text(),
      properties = mapOf(
        "executionResult" to executionResult,
      ),
    )
}
