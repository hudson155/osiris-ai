package osiris.core

import dev.langchain4j.agent.tool.ToolExecutionRequest
import dev.langchain4j.data.message.ToolExecutionResultMessage
import osiris.event.ToolEvent

public abstract class ToolExecutor {
  public abstract suspend fun execute(
    tools: List<Tool<*, *>>,
    executionRequests: List<ToolExecutionRequest>,
  ): List<ToolExecutionResultMessage>

  protected suspend fun execute(
    tools: List<Tool<*, *>>,
    executionRequest: ToolExecutionRequest,
  ): ToolExecutionResultMessage {
    val llmContext = getLlmContext()
    val id = executionRequest.id()
    val toolName = executionRequest.name()
    val input = executionRequest.arguments()
    val tool = requireNotNull(tools.singleNullOrThrow { it.name == toolName }) { "No tool with name: $toolName." }
    llmContext.send(ToolEvent.Start(tool = tool, id = id, input = input))
    val output = tool.execute(input)
    llmContext.send(ToolEvent.End(tool = tool, id = id, input = input, output = output))
    return ToolExecutionResultMessage(id, toolName, output)
  }

  public class Default : ToolExecutor() {
    override suspend fun execute(
      tools: List<Tool<*, *>>,
      executionRequests: List<ToolExecutionRequest>,
    ): List<ToolExecutionResultMessage> =
      executionRequests.map { execute(tools, it) }
  }
}
