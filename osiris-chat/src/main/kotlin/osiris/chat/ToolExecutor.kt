package osiris.chat

import dev.langchain4j.agent.tool.ToolExecutionRequest
import dev.langchain4j.data.message.ToolExecutionResultMessage
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import osiris.tracing.ToolEvent
import osiris.tracing.trace

/**
 * By default, Tools are executed in parallel on using [ToolExecutor.Dispatcher].
 *
 * Alternatively,
 * you can choose to run them on a specific coroutine dispatcher using [ToolExecutor.Dispatcher.dispatcher],
 * choose to run them sequentially using [ToolExecutor.Sequential],
 * or choose to implement your own Tool executor from scratch.
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
    checkNotNull(tool) { "No Tool with name: $toolName." }
    return trace({ ToolEvent.start(tool, executionRequest) }, { ToolEvent.end(it) }) {
      tool.execute(executionRequest)
    }
  }

  public class Dispatcher(
    private val dispatcher: CoroutineDispatcher? = null,
  ) : ToolExecutor() {
    override suspend fun execute(
      tools: List<Tool<*>>,
      executionRequests: List<ToolExecutionRequest>,
    ): List<ToolExecutionResultMessage> =
      coroutineScope {
        executionRequests.map { executionRequest ->
          async(dispatcher ?: EmptyCoroutineContext) {
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
