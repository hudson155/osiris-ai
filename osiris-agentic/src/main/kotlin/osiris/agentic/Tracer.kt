package osiris.agentic

public fun interface Tracer {
  public suspend fun trace(trace: List<Span<*>>)
}
