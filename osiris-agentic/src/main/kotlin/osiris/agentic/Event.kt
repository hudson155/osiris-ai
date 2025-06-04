package osiris.agentic

import dev.langchain4j.data.message.AiMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.single

public sealed class Event {
  public abstract val shouldPropagate: Boolean

  public data class Start(
    val execution: Execution,
  ) : Event() {
    override val shouldPropagate: Boolean = false
  }

  public data class End(
    val execution: Execution,
  ) : Event() {
    override val shouldPropagate: Boolean = false
  }

  public data class AgentStart(
    val agent: Agent,
  ) : Event() {
    override val shouldPropagate: Boolean = true
  }

  public data class AgentEnd(
    val agent: Agent,
  ) : Event() {
    override val shouldPropagate: Boolean = true
  }
}

public suspend fun Flow<Event>.getResponse(): AiMessage {
  val event = filterIsInstance<Event.End>().single()
  return event.execution.messages.last() as AiMessage
}
