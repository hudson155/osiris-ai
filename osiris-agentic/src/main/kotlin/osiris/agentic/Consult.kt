package osiris.agentic

import dev.langchain4j.agent.tool.ToolExecutionRequest
import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.ToolExecutionResultMessage
import dev.langchain4j.data.message.UserMessage
import kairo.lazySupplier.LazySupplier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import osiris.agentic.Consult.Input
import osiris.core.Tool
import osiris.core.convert
import osiris.event.Event
import osiris.event.MessageEvent
import osiris.event.onMessage
import osiris.schema.LlmSchema

public class Consult(
  private val agentName: String,
) : Tool<Input>("consult_$agentName") {
  public data class Input(
    @LlmSchema.Description("The message to the agent.")
    val message: String,
    @LlmSchema.Description("A progress update for the user, regarding their original question.")
    val progressUpdateForUser: String,
  )

  private val agent: LazySupplier<Agent> =
    LazySupplier {
      with(getExecutionContext()) {
        getAgent(agentName)
      }
    }

  override val description: LazySupplier<String?> =
    LazySupplier {
      val agent = agent.get()
      return@LazySupplier agent.description
    }

  override fun execute(executionRequest: ToolExecutionRequest, input: Input): Flow<Event> =
    flow {
      val agent = agent.get()
      val flow = agent.execute(listOf(UserMessage(input.message)))
      var response: ChatMessage? = null
      flow
        .onMessage { response = it }
        .filter { it !is MessageEvent } // TODO: Propagate messages without adding them to the history.
        .onCompletion {
          val executionResult = ToolExecutionResultMessage.from(executionRequest, (response as AiMessage).convert())
          emit(MessageEvent(executionResult))
        }
        .collect(this)
    }
}
