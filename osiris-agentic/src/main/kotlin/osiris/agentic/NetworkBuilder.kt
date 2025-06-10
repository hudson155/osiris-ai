package osiris.agentic

import osiris.event.Event

internal class NetworkImpl(
  override val entrypoint: String?,
  agents: List<Agent>,
  override val listeners: List<(Event) -> Unit>,
) : Network(agents)

public class NetworkBuilder internal constructor() {
  public var entrypoint: String? = null
  public val agents: MutableList<Agent> = mutableListOf()
  public val listeners: MutableList<(event: Event) -> Unit> = mutableListOf()

  internal fun build(): Network =
    NetworkImpl(
      entrypoint = entrypoint,
      agents = agents,
      listeners = listeners,
    )
}

public fun network(block: NetworkBuilder.() -> Unit): Network =
  NetworkBuilder().apply(block).build()
