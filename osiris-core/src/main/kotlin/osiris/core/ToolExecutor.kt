package osiris.core

import dev.langchain4j.agent.tool.ToolExecutionRequest
import dev.langchain4j.data.message.ToolExecutionResultMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

public abstract class ToolExecutor {
  public abstract fun execute(
    tools: List<Tool<*>>,
    executionRequests: List<ToolExecutionRequest>,
  ): Flow<ToolExecutionResultMessage>

  protected suspend fun execute(
    tools: List<Tool<*>>,
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
    override fun execute(
      tools: List<Tool<*>>,
      executionRequests: List<ToolExecutionRequest>,
    ): Flow<ToolExecutionResultMessage> =
      channelFlow {
        executionRequests.map { executionRequest ->
          launch(dispatcher) {
            val executionResponse = execute(tools, executionRequest)
            send(executionResponse)
          }
        }
      }
  }

  public class Serial : ToolExecutor() {
    override fun execute(
      tools: List<Tool<*>>,
      executionRequests: List<ToolExecutionRequest>,
    ): Flow<ToolExecutionResultMessage> =
      flow {
        executionRequests.forEach { executionRequest ->
          val executionResponse = execute(tools, executionRequest)
          emit(executionResponse)
        }
      }
  }
}
