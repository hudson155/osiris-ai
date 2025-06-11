package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import kotlinx.coroutines.withContext
import osiris.core.TraceContext
import osiris.core.trace
import osiris.span.ExecutionEvent
import osiris.span.deriveText

public abstract class Network(
  public val name: String,
  agents: List<Agent>,
) {
  protected abstract val entrypoint: String

  internal val agents: Map<String, Agent> = agents.associateBy { it.name }

  // protected open val listeners: List<Listener> = emptyList() // TODO: Revisit this.

  public suspend fun run(messages: List<ChatMessage>): List<ChatMessage> =
    // val listeners = listeners.map { it.create() } // TODO: Revisit this.
    withContext(TraceContext.create()) {
      trace({ ExecutionEvent(this@Network, deriveText(messages), deriveText(it)) }) {
        val executionContext = ExecutionContext(this@Network)
        withContext(executionContext) {
          val agent = executionContext.getAgent(entrypoint)
          return@withContext agent.execute(messages)
        }
      }
    }

  override fun toString(): String =
    "Network(name=$name)"
}
