package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import kotlinx.coroutines.flow.FlowCollector

@Suppress("LongParameterList")
public class Execution internal constructor(
  internal val network: Network,
  collector: FlowCollector<Event>,
  messages: List<ChatMessage>,
  private val entrypoint: String,
) : FlowCollector<Event> by collector {
  public val messages: MutableList<ChatMessage> = messages.toMutableList()

  internal suspend fun execute() {
    emit(Event.Start(this@Execution))
    val agent = requireNotNull(network.agents[entrypoint]) { "No agent with name $entrypoint." }
    emit(Event.AgentStart(agent))
    agent.execute(this@Execution)
    emit(Event.AgentEnd(agent))
    emit(Event.End(this@Execution))
  }
}
