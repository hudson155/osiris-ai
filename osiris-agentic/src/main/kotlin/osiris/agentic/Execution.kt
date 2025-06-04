package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import kotlinx.coroutines.channels.ProducerScope

@Suppress("LongParameterList")
public class Execution internal constructor(
  internal val network: Network,
  internal val producerScope: ProducerScope<Event>,
  messages: List<ChatMessage>,
  private val entrypoint: String,
) {
  public val messages: MutableList<ChatMessage> = messages.toMutableList()

  internal suspend fun execute() {
    producerScope.send(Event.Start(this@Execution))
    val agent = requireNotNull(network.agents[entrypoint]) { "No agent with name $entrypoint." }
    producerScope.send(Event.AgentStart(agent.name))
    agent.execute(this@Execution)
    producerScope.send(Event.AgentEnd(agent.name))
    producerScope.send(Event.End(this@Execution))
  }
}
