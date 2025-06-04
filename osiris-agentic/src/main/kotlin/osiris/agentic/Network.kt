package osiris.agentic

public class Network

public class NetworkBuilder internal constructor() {
  internal fun build(): Network =
    Network()
}

public fun network(block: NetworkBuilder.() -> Unit): Network =
  NetworkBuilder().apply(block).build()
