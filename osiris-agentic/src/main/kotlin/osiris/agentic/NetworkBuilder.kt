package osiris.agentic

@Suppress("LongParameterList")
internal class NetworkImpl(
  name: String,
  override val entrypoint: String,
  agents: List<Agent>,
  // override val listeners: List<Listener>, // TODO: Revisit this.
) : Network(name, agents)

public class NetworkBuilder internal constructor(
  private val name: String,
) {
  public var entrypoint: String? = null
  public val agents: MutableList<Agent> = mutableListOf()
  // public val listeners: MutableList<Listener> = mutableListOf() // TODO: Revisit this.

  internal fun build(): Network =
    NetworkImpl(
      name = name,
      entrypoint = requireNotNull(entrypoint) { "Network $name must set an entrypoint." },
      agents = agents,
      // listeners = listeners, // TODO: Revisit this.
    )
}

public fun network(name: String, block: NetworkBuilder.() -> Unit): Network =
  NetworkBuilder(name).apply(block).build()
