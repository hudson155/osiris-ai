package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import osiris.event.Event
import osiris.event.ExecutionEvent

public abstract class Network(
  agents: List<Agent>,
) {
  protected open val entrypoint: String? = null

  internal val agents: Map<String, Agent> = agents.associateBy { it.name }

  protected open val listeners: List<(event: Event) -> Unit> = emptyList()

  public constructor(vararg agents: Agent) : this(agents.toList())

  public fun run(
    messages: List<ChatMessage>,
    entrypoint: String? = null,
  ): Flow<Event> =
    channelFlow {
      val context = ExecutionContext(this@Network)
      withContext(context) {
        val agentName = requireNotNull(entrypoint ?: this@Network.entrypoint) { "Network must set an entrypoint." }
        val agent = context.getAgent(agentName)
        send(ExecutionEvent.Start(agentName))
        agent.execute(messages).collect(::send)
        send(ExecutionEvent.End(agentName))
      }
    }.onEach { event ->
      listeners.forEach { it(event) }
    }
}
