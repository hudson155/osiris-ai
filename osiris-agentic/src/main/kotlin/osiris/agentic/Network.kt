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
  protected abstract val entrypoint: String

  /**
   * Listeners help with tracing.
   */
  protected open val listeners: List<Listener> = emptyList()

  internal val agents: Map<String, Agent> = agents.associateBy { it.name }

  public open suspend fun run(
    messages: List<ChatMessage>,
    listeners: List<Listener> = emptyList(),
  ): List<ChatMessage> =
    withTracer(
      tracer = createTracer(listeners),
      start = { TraceEvent.Start("Trace: $name", deriveText(messages)) },
      end = { response -> TraceEvent.End(response?.let { deriveText(it) }) },
    ) {
      logger.debug { "Started execution: (name=$name, messages=$messages)." }
      val executionContext = ExecutionContext(this@Network, messages)
      withContext(executionContext) {
        val agent = executionContext.getAgent(entrypoint)
        return@withContext agent.execute()
      }
      logger.debug { "Ended execution: (name=$name, response=${executionContext.response})." }
      return@withTracer executionContext.response
    }

  private fun createTracer(listeners: List<Listener>): Tracer? {
    if (this.listeners.isEmpty() && listeners.isEmpty()) return null
    return Tracer(this.listeners + listeners)
  }

  override fun toString(): String =
    "Network(name=$name)"
}
