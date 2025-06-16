package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.withContext
import osiris.core.deriveText
import osiris.tracing.Listener
import osiris.tracing.TraceEvent
import osiris.tracing.Tracer
import osiris.tracing.withTracer

private val logger: KLogger = KotlinLogging.logger {}

public abstract class Network(
  public val name: String,
  agents: List<Agent>,
) {
  protected abstract val entrypoint: String

  protected open val listeners: List<Listener> = emptyList()

  internal val agents: Map<String, Agent> = agents.associateBy { it.name }

  public suspend fun run(
    messages: List<ChatMessage>,
    listeners: List<Listener> = emptyList(),
  ): List<ChatMessage> =
    withTracer(
      tracer = Tracer(this.listeners + listeners),
      start = { TraceEvent.Start("Trace: $name", deriveText(messages)) },
      end = { TraceEvent.End(deriveText(it)) },
    ) {
      logger.debug { "Started execution: (name=$name, messages=$messages)." }
      val executionContext = ExecutionContext(this@Network)
      val response = withContext(executionContext) {
        val agent = executionContext.getAgent(entrypoint)
        return@withContext agent.execute(messages)
      }
      logger.debug { "Ended execution: (name=$name, response=$response)." }
      return@withTracer response
    }

  override fun toString(): String =
    "Network(name=$name)"
}
