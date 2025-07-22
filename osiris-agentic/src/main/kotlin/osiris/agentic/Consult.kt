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
  agentName: String,
) : Tool<Input>(prefix + agentName) {
  public data class Input(
    @LlmSchema.Description("The message to the agent.")
    val message: String,
    @LlmSchema.Description("A progress update for the user, regarding their original question.")
    val progressUpdateForUser: String,
  )

  private val agent: LazySupplier<Agent> =
    LazySupplier {
      with(getExecutionContext()) {
        network.getAgent(agentName)
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
    val innerExecutionContext = executionContext.inner(agentName = agent.name, messages = messages)
    withContext(innerExecutionContext) {
      innerExecutionContext.execute()
    }
    return innerExecutionContext.state.get().response.convert()
  }

  public companion object {
    public const val prefix: String = "consult_"
  }
}
