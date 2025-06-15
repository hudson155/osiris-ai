package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.withContext

private val logger: KLogger = KotlinLogging.logger {}

public abstract class Network(
  public val name: String,
  agents: List<Agent>,
) {
  protected abstract val entrypoint: String

  internal val agents: Map<String, Agent> = agents.associateBy { it.name }

  public suspend fun run(messages: List<ChatMessage>): List<ChatMessage> {
    logger.debug { "Started execution: (name=$name, messages=$messages)." }
    val executionContext = ExecutionContext(this@Network)
    val response = withContext(executionContext) {
      val agent = executionContext.getAgent(entrypoint)
      return@withContext agent.execute(messages)
    }
    logger.debug { "Ended execution: (name=$name, response=$response)." }
    return response
  }

  override fun toString(): String =
    "Network(name=$name)"
}
