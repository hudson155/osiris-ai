package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import osiris.tracing.ExecutionEvent
import osiris.tracing.TraceContext
import osiris.tracing.deriveText
import osiris.tracing.getTraceContext
import osiris.tracing.trace

private val logger: KLogger = KotlinLogging.logger {}

public abstract class Network(
  public val name: String,
  agents: List<Agent>,
) {
  protected abstract val entrypoint: String

  internal val agents: Map<String, Agent> = agents.associateBy { it.name }

  protected open val tracers: List<Tracer> = emptyList()

  public suspend fun run(messages: List<ChatMessage>): List<ChatMessage> {
    logger.debug { "Started execution: (name=$name, messages=$messages)." }
    val traceContext = if (tracers.isEmpty()) EmptyCoroutineContext else getTraceContext() ?: TraceContext()
    val response = withContext(traceContext) {
      trace({ ExecutionEvent(this@Network, deriveText(messages), deriveText(it)) }) {
        val executionContext = ExecutionContext(this@Network)
        withContext(executionContext) {
          val agent = executionContext.getAgent(entrypoint)
          return@withContext agent.execute(messages)
        }
      }
    }
    logger.debug { "Ended execution: (name=$name, response=$response)." }
    if (tracers.isNotEmpty()) {
      traceContext as TraceContext
      val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
      tracers.forEach { tracer ->
        scope.launch { tracer.trace(traceContext.trace) }
      }
    }
    return response
  }

  override fun toString(): String =
    "Network(name=$name)"
}
