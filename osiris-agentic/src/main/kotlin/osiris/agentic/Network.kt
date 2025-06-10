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
import osiris.event.deriveText

public abstract class Network(
  public val name: String,
  agents: List<Agent>,
) {
  protected abstract val entrypoint: String

  internal val agents: Map<String, Agent> = agents.associateBy { it.name }

  protected open val listeners: List<Listener> = emptyList()

  public fun run(messages: List<ChatMessage>): Flow<Event> {
    val listeners = listeners.map { it.create() }
    return channelFlow {
      val context = ExecutionContext(this@Network, this)
      withContext(context) {
        val agent = context.getAgent(entrypoint)
        send(ExecutionEvent.Start(this@Network, deriveText(messages)))
        val response = agent.execute(messages).onEach(::send).response().last()
        send(ExecutionEvent.End(deriveText(response)))
      }
    }.onEach { event ->
      listeners.forEach { it(event) }
    }
  }

  override fun toString(): String =
    "Network(name=$name)"
}
