package osiris.agentic

import dev.langchain4j.data.message.AiMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.single

public sealed class Event {
  public data class Start(
    val execution: Execution,
  ) : Event()

  public data class End(
    val execution: Execution,
  ) : Event()

  public data class AgentStart(
    val agentName: String,
  ) : Event()

  public data class AgentEnd(
    val execution: String,
  ) : Event()
}

public suspend fun Flow<Event>.getResponse(): AiMessage {
  val event = filterIsInstance<Event.End>().single()
  return event.execution.messages.last() as AiMessage
}
