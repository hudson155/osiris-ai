package osiris.agentic

import dev.langchain4j.data.message.AiMessage
import osiris.agentic.Consult.Input
import osiris.core.Tool
import osiris.schema.LlmSchema

public class Consult internal constructor(
  private val agentName: String,
) : Tool<Input, String>("consult_$agentName") {
  public data class Input(
    @LlmSchema.Description("The message to the agent.")
    val message: String,
    @LlmSchema.Description("A progress update for the user, regarding their original question.")
    val progressUpdateForUser: String,
  )

  override suspend fun execute(input: Input): String {
    val execution = useExecution()
    val network = execution.network
    val response = network.run(
      messages = buildList {
        addAll(execution.messages)
        add(AiMessage(input.message))
      },
      entrypoint = agentName,
    ).getResponse()
    if (network.settings.includeConsultationProgressUpdates) {
      execution.producerScope.send(Event.ProgressUpdate(input.progressUpdateForUser))
    }
    return response.text()
  }
}

public fun consult(agentName: String): Consult =
  Consult(agentName)
