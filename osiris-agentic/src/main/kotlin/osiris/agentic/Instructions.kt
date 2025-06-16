package osiris.agentic

/**
 * Agent instructions can be provided asynchronously,
 * so that they can be fetched from external sources.
 */
public fun interface Instructions {
  public suspend fun get(): String
}
