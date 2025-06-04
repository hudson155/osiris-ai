package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.withContext

internal class Execution(
  internal val network: Network,
  private val producerScope: ProducerScope<Event>,
  messages: List<ChatMessage>,
  private val entrypoint: String,
) : AbstractCoroutineContextElement(Key) {
  internal val messages: MutableList<ChatMessage> = messages.toMutableList()

  suspend fun execute() {
    withContext(this) {
      producerScope.send(Event.Start(this@Execution))
      val agent = requireNotNull(network.agents[entrypoint]) { "No agent with name $entrypoint." }
      producerScope.send(Event.AgentStart(agent.name))
      agent.execute()
      producerScope.send(Event.AgentEnd(agent.name))
      producerScope.send(Event.End(this@Execution))
    }
  }

  internal companion object Key : CoroutineContext.Key<Execution>
}

internal suspend fun useExecution(): Execution =
  checkNotNull(coroutineContext[Execution.Key])
