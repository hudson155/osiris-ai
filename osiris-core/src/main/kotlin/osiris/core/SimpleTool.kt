package osiris.core

import dev.langchain4j.agent.tool.ToolExecutionRequest
import dev.langchain4j.data.message.ToolExecutionResultMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import osiris.event.Event
import osiris.event.MessageEvent

public abstract class SimpleTool<in Input : Any>(name: String) : Tool<Input>(name) {
  override fun execute(executionRequest: ToolExecutionRequest, input: Input): Flow<Event> =
    flow {
      val outputString = execute(input)
      val executionResult = ToolExecutionResultMessage.from(executionRequest, outputString)
      emit(MessageEvent(executionResult))
    }

  public abstract suspend fun execute(input: Input): String
}
