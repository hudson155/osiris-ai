package osiris.agentic

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * The execution context is available via [getExecutionContext]
 * at any point within a [Network.run] call (an "execution").
 * A common use case is to allow Tools like [Consult] to "reach into" the Network
 * and access specific properties.
 *
 * Although this class is public, API stability is not guaranteed.
 */
public class ExecutionContext(
  private val network: Network,
) : AbstractCoroutineContextElement(key) {
  internal fun getAgent(agentName: String): Agent =
    requireNotNull(network.agents[agentName]) { "No Agent with name $agentName." }

  public companion object {
    public val key: CoroutineContext.Key<ExecutionContext> =
      object : CoroutineContext.Key<ExecutionContext> {}
  }
}

public suspend fun getExecutionContext(): ExecutionContext =
  checkNotNull(coroutineContext[ExecutionContext.key])
