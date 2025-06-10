package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import osiris.core.response
import osiris.event.Event
import osiris.event.ExecutionEvent

public abstract class Network(
  agents: List<Agent>,
) {
  protected open val entrypoint: String? = null

  internal val agents: Map<String, Agent> = agents.associateBy { it.name }

  protected open val listeners: List<Listener> = emptyList()

  public fun run(
    messages: List<ChatMessage>,
    entrypoint: String? = null,
  ): Flow<Event> {
    val listeners = listeners.map { it.create() }
    return channelFlow {
      val context = ExecutionContext(this@Network, this)
      withContext(context) {
        val agentName = requireNotNull(entrypoint ?: this@Network.entrypoint) { "Network must set an entrypoint." }
        val agent = context.getAgent(agentName)
        send(ExecutionEvent.Start(agentName, messages))
        val response = agent.execute(messages).onEach(::send).response().last()
        send(ExecutionEvent.End(response.text()))
      }
    }.onEach { event ->
      listeners.forEach { it(event) }
    }
  }
}
