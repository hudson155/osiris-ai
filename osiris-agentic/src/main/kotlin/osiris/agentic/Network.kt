package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

public class Network internal constructor(
  private val entrypoint: String?,
  internal val agents: Map<String, Agent>,
) {
  public fun run(
    messages: List<ChatMessage>,
    entrypoint: String? = null,
  ): Flow<Event> {
    @Suppress("NoNameShadowing")
    val entrypoint = requireNotNull(entrypoint ?: this.entrypoint) { "Network must set an entrypoint." }
    return flow {
      val execution = Execution(
        network = this@Network,
        collector = this,
        messages = messages,
        entrypoint = entrypoint,
      )
      execution.execute()
    }
  }
}

public class NetworkBuilder internal constructor() {
  public var entrypoint: String? = null
  public val agents: MutableList<Agent> = mutableListOf()

  internal fun build(): Network =
    Network(
      entrypoint = entrypoint,
      agents = agents.associateBy { it.name },
    )
}

public fun network(block: NetworkBuilder.() -> Unit): Network =
  NetworkBuilder().apply(block).build()
