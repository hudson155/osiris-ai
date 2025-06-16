package osiris.agentic

import osiris.tracing.Listener

@Suppress("LongParameterList")
internal class NetworkImpl(
  name: String,
  override val entrypoint: String,
  agents: List<Agent>,
  override val listeners: List<Listener>,
) : Network(name, agents)

public class NetworkBuilder internal constructor(
  private val name: String,
) {
  public var entrypoint: String? = null
  public val agents: MutableList<Agent> = mutableListOf()
  private val listeners: MutableList<Listener> = mutableListOf()

  public fun listener(listener: Listener) {
    listeners += listener
  }

  internal fun build(): Network =
    NetworkImpl(
      name = name,
      entrypoint = requireNotNull(entrypoint) { "Network $name must set an entrypoint." },
      agents = agents,
      listeners = listeners,
    )
}

/**
 * Helper DSL to build an [Agent].
 */
public fun network(name: String, block: NetworkBuilder.() -> Unit): Network =
  NetworkBuilder(name).apply(block).build()
