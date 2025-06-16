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
  /**
   * The name of the Agent to be visited first.
   */
  public var entrypoint: String? = null

  /**
   * All Agents within the Network.
   */
  public val agents: MutableList<Agent> = mutableListOf()

  /**
   * Listeners help with tracing.
   */
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
public fun network(
  /**
   * The Agent's name identifies it.
   */
  name: String,
  block: NetworkBuilder.() -> Unit,
): Network =
  NetworkBuilder(name).apply(block).build()
