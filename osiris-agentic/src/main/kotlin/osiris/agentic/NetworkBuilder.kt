package osiris.agentic

import osiris.tracing.Tracer

@Suppress("LongParameterList")
internal class NetworkImpl(
  name: String,
  override val entrypoint: String,
  agents: List<Agent>,
  override val tracer: Tracer?,
) : Network(name, agents)

public class NetworkBuilder internal constructor(
  private val name: String,
) {
  public var entrypoint: String? = null
  public val agents: MutableList<Agent> = mutableListOf()
  public var tracer: Tracer? = null

  internal fun build(): Network =
    NetworkImpl(
      name = name,
      entrypoint = requireNotNull(entrypoint) { "Network $name must set an entrypoint." },
      agents = agents,
      tracer = tracer,
    )
}

public fun network(name: String, block: NetworkBuilder.() -> Unit): Network =
  NetworkBuilder(name).apply(block).build()
