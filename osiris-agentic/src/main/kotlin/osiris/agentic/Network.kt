package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

public class Network internal constructor(
  private val entrypoint: String?,
  internal val agents: Map<String, Agent>,
  internal val settings: Settings,
) {
  public fun run(
    messages: List<ChatMessage>,
    entrypoint: String? = null,
  ): Flow<Event> {
    @Suppress("NoNameShadowing")
    val entrypoint = requireNotNull(entrypoint ?: this.entrypoint) { "Network must set an entrypoint." }
    return channelFlow {
      val execution = Execution(
        network = this@Network,
        producerScope = this,
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
  private val settings: SettingsBuilder = SettingsBuilder()

  public fun settings(block: SettingsBuilder.() -> Unit) {
    settings.apply(block)
  }

  internal fun build(): Network =
    Network(
      entrypoint = entrypoint,
      agents = agents.associateBy { it.name },
      settings = settings.build(),
    )
}

public fun network(block: NetworkBuilder.() -> Unit): Network =
  NetworkBuilder().apply(block).build()
