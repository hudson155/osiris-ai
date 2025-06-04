package osiris.core

import dev.langchain4j.agent.tool.ToolExecutionRequest
import dev.langchain4j.data.message.ToolExecutionResultMessage

public abstract class ToolExecutor {
  public abstract suspend fun execute(
    tools: List<Tool<*, *>>,
    executionRequests: List<ToolExecutionRequest>,
  ): List<ToolExecutionResultMessage>

  public class Default : ToolExecutor() {
    override suspend fun execute(
      tools: List<Tool<*, *>>,
      executionRequests: List<ToolExecutionRequest>,
    ): List<ToolExecutionResultMessage> {
      val tools = tools.associateBy { it.name }
      return executionRequests.map { executionRequest ->
        val id = executionRequest.id()
        val toolName = executionRequest.name()
        val input = executionRequest.arguments()
        val tool = checkNotNull(tools[toolName]) { "No tool with name: $toolName." }
        val output = tool.execute(input)
        return@map ToolExecutionResultMessage(id, toolName, output)
      }
    }
  }
}
