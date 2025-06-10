package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import osiris.core.aiResponses
import osiris.event.Event

public abstract class Network(
  agents: List<Agent>,
) {
  protected open val entrypoint: String? = null

  internal val agents: Map<String, Agent> = agents.associateBy { it.name }

  public constructor(vararg agents: Agent) : this(agents.toList())

  public fun run(
    messages: List<ChatMessage>,
    entrypoint: String? = null,
  ): Flow<Event> =
    channelFlow {
      val context = ExecutionContext(this@Network, this)
      withContext(context) {
        val agent = context.getAgent(
          agentName = requireNotNull(entrypoint ?: this@Network.entrypoint) { "Network must set an entrypoint." },
        )
        agent.execute(messages).onEach(::send).aiResponses().first()
      }
    }
}
