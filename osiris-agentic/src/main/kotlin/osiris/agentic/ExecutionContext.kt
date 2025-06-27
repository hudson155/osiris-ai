package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.currentCoroutineContext

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
  public val messages: List<ChatMessage>,
) : AbstractCoroutineContextElement(ExecutionContext) {
  internal val response: MutableList<ChatMessage> = mutableListOf()

  internal fun getAgent(agentName: String): Agent =
    requireNotNull(network.agents[agentName]) { "No Agent with name $agentName." }

  internal fun withMessages(messages: List<ChatMessage>): ExecutionContext =
    ExecutionContext(
      network = network,
      messages = messages,
    )

  public companion object : CoroutineContext.Key<ExecutionContext>
}

public suspend fun getExecutionContext(): ExecutionContext =
  checkNotNull(currentCoroutineContext()[ExecutionContext])
