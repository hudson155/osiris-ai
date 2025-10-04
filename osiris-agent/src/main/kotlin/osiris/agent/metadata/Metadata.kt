package osiris.agent.metadata

import io.ktor.util.AttributeKey
import osiris.agent.Context

public abstract class Metadata {
  public abstract suspend fun get(): Map<String, String>

  public abstract suspend fun set(key: String, value: String)
}

private val key: AttributeKey<Metadata> = AttributeKey("metadata")

public var Context.metadata: Metadata
  get() = computeIfAbsent(key) { NoopMetadata() }
  set(value) {
    put(key, value)
  }
