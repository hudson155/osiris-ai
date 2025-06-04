package osiris.agentic

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import dev.langchain4j.data.message.AiMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.single

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  JsonSubTypes.Type(Event.Start::class, "Start"),
  JsonSubTypes.Type(Event.End::class, "End"),
  JsonSubTypes.Type(Event.AgentStart::class, "AgentStart"),
  JsonSubTypes.Type(Event.AgentEnd::class, "AgentEnd"),
  JsonSubTypes.Type(Event.Consult::class, "Consult"),
)
public sealed class Event {
  public data class Start(
    @JsonIgnore val execution: Execution,
  ) : Event()

  public data class End(
    @JsonIgnore val execution: Execution,
  ) : Event()

  public data class AgentStart(
    val agentName: String,
  ) : Event()

  public data class AgentEnd(
    val execution: String,
  ) : Event()

  public data class Consult(
    val input: Consult.Input,
  ) : Event()
}

public suspend fun Flow<Event>.getResponse(): AiMessage {
  val event = filterIsInstance<Event.End>().single()
  return event.execution.messages.last() as AiMessage
}
