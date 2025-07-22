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
public class ExecutionContext internal constructor(
  public val network: Network,
  currentAgent: Agent,
  internal val messages: List<ChatMessage>,
) : AbstractCoroutineContextElement(ExecutionContext) {
  public var currentAgent: Agent = currentAgent
    private set

  public var response: List<ChatMessage> = emptyList()
    internal set

  public suspend fun execute() {
    currentAgent.execute()
  }

  public fun inner(
    agentName: String,
    messages: List<ChatMessage>? = null,
  ): ExecutionContext =
    ExecutionContext(
      network = network,
      currentAgent = network.getAgent(agentName),
      messages = messages ?: this.messages,
    )

  public companion object : CoroutineContext.Key<ExecutionContext>
}

public suspend fun getExecutionContext(): ExecutionContext =
  checkNotNull(currentCoroutineContext()[ExecutionContext])
