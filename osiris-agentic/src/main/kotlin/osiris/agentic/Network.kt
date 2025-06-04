package osiris.agentic

public class Network(
  public val agents: Map<String, Agent>,
)

public class NetworkBuilder internal constructor() {
  public val agents: MutableList<Agent> = mutableListOf()

  internal fun build(): Network =
    Network(
      agents = agents.associateBy { it.name },
    )
}

public fun network(block: NetworkBuilder.() -> Unit): Network =
  NetworkBuilder().apply(block).build()
