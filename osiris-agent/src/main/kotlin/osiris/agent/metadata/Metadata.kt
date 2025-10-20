package osiris.agent.metadata

import io.ktor.util.AttributeKey
import osiris.agent.Context

public abstract class Metadata {
  public abstract suspend fun get(): Map<String, String>

  public abstract suspend fun set(key: String, value: String?)

  public open suspend fun transform(key: String, transform: (value: String?) -> String?) {
    val value = get()[key]
    set(key, transform(value))
  }
}

private val key: AttributeKey<Metadata> = AttributeKey("metadata")

public var Context.metadata: Metadata
  get() = computeIfAbsent(key) { NoopMetadata() }
  set(value) {
    put(key, value)
  }
