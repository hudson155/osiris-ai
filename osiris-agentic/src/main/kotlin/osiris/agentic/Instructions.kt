package osiris.agentic

public fun interface Instructions {
  public suspend fun get(): String
}
