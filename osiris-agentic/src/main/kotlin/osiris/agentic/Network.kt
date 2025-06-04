package osiris.agentic

public class Network(
  public val entrypoint: String?,
  public val agents: Map<String, Agent>,
)

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
