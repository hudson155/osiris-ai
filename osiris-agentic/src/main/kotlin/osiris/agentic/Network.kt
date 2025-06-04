package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

public class Network(
  public val entrypoint: String?,
  public val agents: Map<String, Agent>,
) {
  public fun run(
    messages: List<ChatMessage>,
    entrypoint: String? = null,
  ): Flow<NetworkEvent> {
    val entrypoint = requireNotNull(entrypoint ?: this.entrypoint) { "Network must set an entrypoint." }
    return channelFlow {
      send(NetworkEvent.Start)
      val agent = requireNotNull(agents[entrypoint]) { "No agent with name $entrypoint." }
      send(NetworkEvent.AgentStart(agent.name))
      val response = agent.run(messages)
      send(NetworkEvent.AgentEnd(agent.name))
      send(NetworkEvent.End(response))
    }
  }
}

public class NetworkBuilder internal constructor() {
  public var entrypoint: String? = null
  public val agents: MutableList<Agent> = mutableListOf()

  internal fun build(): Network =
    Network(
      entrypoint = entrypoint,
      agents = agents.associateBy { it.name },
    )
}

public fun network(block: NetworkBuilder.() -> Unit): Network =
  NetworkBuilder().apply(block).build()
