package osiris.core

import dev.langchain4j.agent.tool.ToolExecutionRequest
import dev.langchain4j.data.message.ToolExecutionResultMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import osiris.tracing.ToolEvent
import osiris.tracing.trace

/**
 * By default, tools are executed in parallel on [Dispatchers.IO] using [ToolExecutor.Dispatcher].
 *
 * Alternatively,
 * you can choose to run them on a different coroutine dispatcher using [ToolExecutor.Dispatcher.dispatcher],
 * choose to run them sequentially using [ToolExecutor.Sequential],
 * or choose to implement your own tool executor from scratch.
 */
public abstract class ToolExecutor {
  public abstract suspend fun execute(
    tools: List<Tool<*>>,
    executionRequests: List<ToolExecutionRequest>,
  ): List<ToolExecutionResultMessage>

  public suspend fun execute(
    tools: List<Tool<*>>,
    executionRequest: ToolExecutionRequest,
  ): ToolExecutionResultMessage {
    val toolName = executionRequest.name()
    val tool = tools.singleNullOrThrow { it.name == toolName }
    checkNotNull(tool) { "No tool with name: $toolName." }
    return trace({ ToolEvent.Start(tool, executionRequest) }, { ToolEvent.End(it) }) {
      tool.execute(executionRequest)
    }
  }

  public class Dispatcher(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
  ) : ToolExecutor() {
    override suspend fun execute(
      tools: List<Tool<*>>,
      executionRequests: List<ToolExecutionRequest>,
    ): List<ToolExecutionResultMessage> =
      coroutineScope {
        executionRequests.map { executionRequest ->
          async(dispatcher) {
            execute(tools, executionRequest)
          }
        }.awaitAll()
      }
  }

  public class Sequential : ToolExecutor() {
    override suspend fun execute(
      tools: List<Tool<*>>,
      executionRequests: List<ToolExecutionRequest>,
    ): List<ToolExecutionResultMessage> =
      executionRequests.map { executionRequest ->
        execute(tools, executionRequest)
      }
  }
}
