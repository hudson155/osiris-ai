package osiris.core

import dev.langchain4j.agent.tool.ToolExecutionRequest
import dev.langchain4j.data.message.ToolExecutionResultMessage
import kotlinx.coroutines.channels.ProducerScope
import osiris.event.Event
import osiris.event.ToolEvent

public abstract class ToolExecutor {
  public abstract suspend fun execute(
    tools: List<Tool<*, *>>,
    producer: ProducerScope<Event>,
    executionRequests: List<ToolExecutionRequest>,
  ): List<ToolExecutionResultMessage>

  public class Default : ToolExecutor() {
    override suspend fun execute(
      tools: List<Tool<*, *>>,
      producer: ProducerScope<Event>,
      executionRequests: List<ToolExecutionRequest>,
    ): List<ToolExecutionResultMessage> =
      executionRequests.map { executionRequest ->
        val id = executionRequest.id()
        val toolName = executionRequest.name()
        val input = executionRequest.arguments()
        val tool = requireNotNull(tools.singleNullOrThrow { it.name == toolName }) { "No tool with name: $toolName." }
        producer.send(ToolEvent.Start(tool = tool, id = id, input = input))
        val output = tool.execute(input)
        producer.send(ToolEvent.End(tool = tool, id = id, input = input, output = output))
        return@map ToolExecutionResultMessage(id, toolName, output)
      }
  }
}
