package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import osiris.event.Event
import osiris.event.onMessage

private val logger: KLogger = KotlinLogging.logger {}

public abstract class Network(
  public val name: String,
  agents: List<Agent>,
) {
  protected abstract val entrypoint: String

  internal val agents: Map<String, Agent> = agents.associateBy { it.name }

  public fun run(messages: List<ChatMessage>): Flow<Event> {
    logger.debug { "Started execution: (name=$name, messages=$messages)." }
    val executionContext = ExecutionContext(this@Network)
    val agent = executionContext.getAgent(entrypoint)
    val flow = agent.execute(messages).flowOn(executionContext)
    var response: ChatMessage? = null
    return flow
      .onMessage { response += it }
      .onCompletion {
        logger.debug { "Ended execution: (name=$name, response=${checkNotNull(response)})." }
      }
  }

  override fun toString(): String =
    "Network(name=$name)"
}
