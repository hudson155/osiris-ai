package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.withContext
import osiris.core.TraceContext
import osiris.core.trace
import osiris.span.ExecutionEvent
import osiris.span.Span
import osiris.span.deriveText

private val logger: KLogger = KotlinLogging.logger {}

public abstract class Network(
  public val name: String,
  agents: List<Agent>,
) {
  protected abstract val entrypoint: String

  internal val agents: Map<String, Agent> = agents.associateBy { it.name }

  // protected open val listeners: List<Listener> = emptyList() // TODO: Revisit this.

  public suspend fun run(messages: List<ChatMessage>): Pair<List<ChatMessage>, List<Span<*>>> {
    logger.debug { "Started execution: (name=$name, messages=$messages)." }
    // val listeners = listeners.map { it.create() } // TODO: Revisit this.
    val traceContext = TraceContext.create()
    return withContext(traceContext) {
      val response = trace({ ExecutionEvent(this@Network, deriveText(messages), deriveText(it)) }) {
        val executionContext = ExecutionContext(this@Network)
        withContext(executionContext) {
          val agent = executionContext.getAgent(entrypoint)
          return@withContext agent.execute(messages)
        }
      }
      return@withContext Pair(response, traceContext.spans)
    }.also { response ->
      logger.debug { "Ended execution: (name=$name, response=$response)." }
    }
  }

  override fun toString(): String =
    "Network(name=$name)"
}
