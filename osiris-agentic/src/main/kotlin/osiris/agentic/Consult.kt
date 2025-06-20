package osiris.agentic

import dev.langchain4j.data.message.UserMessage
import kairo.lazySupplier.LazySupplier
import kotlinx.coroutines.withContext
import osiris.agentic.Consult.Input
import osiris.chat.Tool
import osiris.chat.convert
import osiris.schema.LlmSchema

/**
 * Use this Tool to allow an Agent to consult another Agent.
 */
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

  override suspend fun execute(input: Input): String {
    val executionContext = getExecutionContext()
    val agent = agent.get()
    val messages = listOf(UserMessage(input.message))
    val innerExecutionContext = executionContext.withMessages(messages)
    withContext(innerExecutionContext) {
      agent.execute()
    }
    return innerExecutionContext.response.convert()
  }
}
