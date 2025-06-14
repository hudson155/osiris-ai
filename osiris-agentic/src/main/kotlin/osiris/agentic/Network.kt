package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import osiris.event.Event
import osiris.event.MessageEvent

private val logger: KLogger = KotlinLogging.logger {}

public abstract class Network(
  public val name: String,
  agents: List<Agent>,
) {
  protected abstract val entrypoint: String

  internal val agents: Map<String, Agent> = agents.associateBy { it.name }

  public suspend fun run(messages: List<ChatMessage>): Flow<Event> {
    logger.debug { "Started execution: (name=$name, messages=$messages)." }
    val executionContext = ExecutionContext(this@Network)
    val agent = executionContext.getAgent(entrypoint)
    val flow = agent.execute(messages).flowOn(executionContext)
    var response: ChatMessage? = null
    return flow
      .onEach { event ->
        if (event !is MessageEvent) return@onEach
        response = event.message
      }
      .onCompletion { logger.debug { "Ended execution: (name=$name, response=$response)." } }
  }

  override fun toString(): String =
    "Network(name=$name)"
}
