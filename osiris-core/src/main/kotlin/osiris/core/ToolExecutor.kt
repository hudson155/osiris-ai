package osiris.core

import dev.langchain4j.agent.tool.ToolExecutionRequest
import dev.langchain4j.data.message.ToolExecutionResultMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

public abstract class ToolExecutor {
  public abstract suspend fun execute(
    tools: List<Tool<*, *>>,
    executionRequests: List<ToolExecutionRequest>,
  ): List<ToolExecutionResultMessage>

  protected suspend fun execute(
    tools: List<Tool<*, *>>,
    executionRequest: ToolExecutionRequest,
  ): ToolExecutionResultMessage {
    val id = executionRequest.id()
    val toolName = executionRequest.name()
    val tool = requireNotNull(tools.singleNullOrThrow { it.name == toolName }) { "No tool with name: $toolName." }
    val output = tool.execute(id, executionRequest.arguments())
    return ToolExecutionResultMessage(id, toolName, output)
  }

  public class Dispatcher(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
  ) : ToolExecutor() {
    override suspend fun execute(
      tools: List<Tool<*, *>>,
      executionRequests: List<ToolExecutionRequest>,
    ): List<ToolExecutionResultMessage> =
      coroutineScope {
        executionRequests.map { async(dispatcher) { execute(tools, it) } }.awaitAll()
      }
  }

  public class Serial : ToolExecutor() {
    override suspend fun execute(
      tools: List<Tool<*, *>>,
      executionRequests: List<ToolExecutionRequest>,
    ): List<ToolExecutionResultMessage> =
      executionRequests.map { execute(tools, it) }
  }
}
