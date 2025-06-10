package osiris.agentic

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.channels.ProducerScope
import osiris.event.Event

public class ExecutionContext(
  private val network: Network,
  producer: ProducerScope<Event>,
) : AbstractCoroutineContextElement(key), ProducerScope<Event> by producer {
  internal fun getAgent(agentName: String): Agent =
    requireNotNull(network.agents[agentName]) { "No agent with name $agentName." }

  public companion object {
    public val key: CoroutineContext.Key<ExecutionContext> =
      object : CoroutineContext.Key<ExecutionContext> {}
  }
}

public suspend fun getExecutionContext(): ExecutionContext =
  checkNotNull(coroutineContext[ExecutionContext.key])
