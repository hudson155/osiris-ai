package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.withContext

@Suppress("LongParameterList")
public class Execution internal constructor(
  internal val network: Network,
  internal val producerScope: ProducerScope<Event>,
  messages: List<ChatMessage>,
  private val entrypoint: String,
) : AbstractCoroutineContextElement(Key) {
  public val messages: MutableList<ChatMessage> = messages.toMutableList()

  internal suspend fun execute() {
    withContext(this) {
      producerScope.send(Event.Start(this@Execution))
      val agent = requireNotNull(network.agents[entrypoint]) { "No agent with name $entrypoint." }
      producerScope.send(Event.AgentStart(agent.name))
      agent.execute()
      producerScope.send(Event.AgentEnd(agent.name))
      producerScope.send(Event.End(this@Execution))
    }
  }

  public companion object Key : CoroutineContext.Key<Execution>
}

internal suspend fun useExecution(): Execution =
  checkNotNull(coroutineContext[Execution.Key])
