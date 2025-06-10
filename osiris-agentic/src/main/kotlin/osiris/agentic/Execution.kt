package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import kotlinx.coroutines.channels.ProducerScope

@Suppress("LongParameterList")
public class Execution internal constructor(
  internal val network: Network,
  producer: ProducerScope<Event>,
  messages: List<ChatMessage>,
  private val entrypoint: String,
) : ProducerScope<Event> by producer {
  public val messages: MutableList<ChatMessage> = messages.toMutableList()

  internal suspend fun execute() {
    send(Event.Start(this@Execution))
    val agent = requireNotNull(network.agents[entrypoint]) { "No agent with name $entrypoint." }
    send(Event.AgentStart(agent))
    agent.execute(this@Execution)
    send(Event.AgentEnd(agent))
    send(Event.End(this@Execution))
  }
}
