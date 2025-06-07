package osiris.agentic

internal class NetworkImpl(
  override val entrypoint: String?,
  agents: List<Agent>,
) : Network(agents)

public class NetworkBuilder internal constructor() {
  public var entrypoint: String? = null
  public val agents: MutableList<Agent> = mutableListOf()

  internal fun build(): Network =
    NetworkImpl(
      entrypoint = entrypoint,
      agents = agents,
    )
}

public fun network(block: NetworkBuilder.() -> Unit): Network =
  NetworkBuilder().apply(block).build()
