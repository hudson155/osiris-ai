package osiris.agentic

import dev.langchain4j.data.message.AiMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.single

public sealed class NetworkEvent {
  public data object Start : NetworkEvent()

  public data class End(
    val response: AiMessage,
  ) : NetworkEvent()

  public data class AgentStart(
    val agentName: String,
  ) : NetworkEvent()

  public data class AgentEnd(
    val agentName: String,
  ) : NetworkEvent()
}

public suspend fun Flow<NetworkEvent>.getResponse(): AiMessage =
  filterIsInstance<NetworkEvent.End>().single().response
