package osiris.agentic

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

public class ExecutionContext(
  private val network: Network,
) : AbstractCoroutineContextElement(key) {
  internal fun getAgent(agentName: String): Agent =
    requireNotNull(network.agents[agentName]) { "No agent with name $agentName." }

  public companion object {
    public val key: CoroutineContext.Key<ExecutionContext> =
      object : CoroutineContext.Key<ExecutionContext> {}
  }
}

public suspend fun getExecutionContext(): ExecutionContext =
  checkNotNull(coroutineContext[ExecutionContext.key])
