package osiris.core

import dev.langchain4j.agent.tool.ToolExecutionRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import osiris.event.Event

/**
 * By default, tools are executed in parallel on [Dispatchers.IO] using [ToolExecutor.Dispatcher].
 *
 * Alternatively,
 * you can choose to run them on a different coroutine dispatcher using [ToolExecutor.Dispatcher.dispatcher],
 * choose to run them sequentially using [ToolExecutor.Serial],
 * or choose to implement your own tool executor from scratch.
 */
public abstract class ToolExecutor {
  public abstract fun execute(
    tools: List<Tool<*>>,
    executionRequests: List<ToolExecutionRequest>,
  ): Flow<Event>

  protected fun execute(
    tools: List<Tool<*>>,
    executionRequest: ToolExecutionRequest,
  ): Flow<Event> {
    val toolName = executionRequest.name()
    val tool = checkNotNull(tools.singleNullOrThrow { it.name == toolName }) { "No tool with name: $toolName." }
    return tool.execute(executionRequest)
  }

  public class Dispatcher(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
  ) : ToolExecutor() {
    override fun execute(
      tools: List<Tool<*>>,
      executionRequests: List<ToolExecutionRequest>,
    ): Flow<Event> =
      channelFlow {
        executionRequests.map { executionRequest ->
          launch(dispatcher) {
            execute(tools, executionRequest)
              .collect { send(it) }
          }
        }
      }
  }

  public class Serial : ToolExecutor() {
    override fun execute(
      tools: List<Tool<*>>,
      executionRequests: List<ToolExecutionRequest>,
    ): Flow<Event> =
      flow {
        executionRequests.forEach { executionRequest ->
          execute(tools, executionRequest)
            .collect { emit(it) }
        }
      }
  }
}
