package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.withContext
import osiris.chat.deriveText
import osiris.tracing.Listener
import osiris.tracing.TraceEvent
import osiris.tracing.Tracer
import osiris.tracing.withTracer

private val logger: KLogger = KotlinLogging.logger {}

/**
 * Agents live within a Network,
 * which enables Agents to consult one another to complete complex tasks.
 */
public abstract class Network(
  /**
   * The Agent's name identifies it.
   */
  public val name: String,
  /**
   * All Agents within the Network.
   */
  agents: List<Agent>,
) {
  /**
   * The name of the Agent to be visited first.
   */
  protected open val entrypoint: String? = null

  /**
   * Listeners help with tracing.
   */
  protected open val listeners: List<Listener> = emptyList()

  private val agents: Map<String, Agent> = agents.associateBy { it.name }

  public fun getAgent(agentName: String): Agent =
    requireNotNull(getAgentOrNull(agentName)) { "No Agent with name $agentName." }

  public fun getAgentOrNull(agentName: String): Agent? =
    agents[agentName]

  public suspend fun run(
    messages: List<ChatMessage>,
    entrypoint: String? = null,
    listeners: List<Listener> = emptyList(),
  ): NetworkState =
    withTracer(
      tracer = createTracer(listeners),
      buildStart = { TraceEvent.start(name, deriveText(messages)) },
      buildEnd = { TraceEvent.end(deriveText(it.messages)) },
    ) {
      logger.debug { "Started execution: (name=$name, messages=$messages)." }
      val executionContext = ExecutionContext(
        network = this@Network,
        currentAgent = getAgent(checkNotNull(entrypoint ?: this.entrypoint) { "No entrypoint specified." }),
        messages = messages,
      )
      withContext(executionContext) {
        executionContext.execute()
      }
      logger.debug { "Ended execution: (name=$name, response=${executionContext.state.get().messages})." }
      return@withTracer executionContext.state.get()
    }

  private fun createTracer(listeners: List<Listener>): Tracer? {
    if (this.listeners.isEmpty() && listeners.isEmpty()) return null
    return Tracer(this.listeners + listeners)
  }

  override fun toString(): String =
    "Network(name=$name)"
}
