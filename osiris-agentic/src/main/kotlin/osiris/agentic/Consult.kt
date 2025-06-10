package osiris.agentic

import dev.langchain4j.data.message.AiMessage
import kairo.lazySupplier.LazySupplier
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach
import osiris.agentic.Consult.Input
import osiris.core.Tool
import osiris.core.response
import osiris.schema.LlmSchema

public class Consult(
  private val agentName: String,
) : Tool<Input, String>("consult_$agentName") {
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
    val context = getExecutionContext()
    val agent = agent.get()
    val response = agent.execute(
      messages = listOf(AiMessage(input.message)),
    ).onEach(context::send).response().last()
    return response.text()
  }
}
