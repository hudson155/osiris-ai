package osiris.agentic

import dev.langchain4j.data.message.AiMessage
import osiris.agentic.Consult.Input
import osiris.core.Tool
import osiris.schema.LlmSchema

public class Consult internal constructor(
  private val agentName: String,
  override val description: String?,
  private val execution: Execution,
) : Tool<Input, String>("consult_$agentName") {
  public data class Input(
    @LlmSchema.Description("The message to the agent.")
    val message: String,
    @LlmSchema.Description("A progress update for the user, regarding their original question.")
    val progressUpdateForUser: String,
  )

  override suspend fun execute(input: Input): String {
    val network = execution.network
    val response = network.run(
      messages = listOf(AiMessage(input.message)),
      entrypoint = agentName,
    ).getResponse()
    execution.producerScope.send(Event.Consult(input))
    return response.text()
  }
}

public fun consult(
  agentName: String,
  description: String? = null,
): ToolProvider =
  ToolProvider { execution ->
    val agent = requireNotNull(execution.network.agents[agentName]) { "No agent with name $agentName." }
    return@ToolProvider Consult(
      agentName = agentName,
      description = description ?: agent.description,
      execution = execution,
    )
  }
