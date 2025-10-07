package osiris.agent.metadata

public class NoopMetadata : Metadata() {
  override suspend fun get(): Map<String, String> =
    emptyMap()

  override suspend fun set(key: String, value: String?): Unit =
    Unit

  override suspend fun transform(key: String, transform: (value: String?) -> String?): Unit =
    Unit
}
