package osiris.agentic

import kairo.lazySupplier.LazySupplier
import osiris.agentic.Handoff.Input
import osiris.chat.Tool
import osiris.schema.LlmSchema

/**
 * Use this Tool to allow an Agent to hand off control to another Agent.
 */
public class Handoff(
  agentName: String,
) : Tool<Input>(prefix + agentName) {
  public data class Input(
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
    executionContext.state.updateAndGet { state ->
      state.copy(
        currentAgent = agent,
        llmExit = true,
      )
    }
    return "Successfully handed off to ${agent.name}."
  }

  public companion object {
    public const val prefix: String = "handoff_"
  }
}
