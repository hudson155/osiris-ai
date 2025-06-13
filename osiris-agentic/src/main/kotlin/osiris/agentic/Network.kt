package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.withContext
import osiris.core.trace
import osiris.tracing.ExecutionEvent
import osiris.tracing.deriveText

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
    val response = trace({ ExecutionEvent(this@Network, deriveText(messages), deriveText(it)) }) {
      val executionContext = ExecutionContext(this@Network)
      withContext(executionContext) {
        val agent = executionContext.getAgent(entrypoint)
        return@withContext agent.execute(messages)
      }
    }
    logger.debug { "Ended execution: (name=$name, response=$response)." }
    return response
  }

  override fun toString(): String =
    "Network(name=$name)"
}
