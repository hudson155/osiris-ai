package osiris.agentic

import osiris.tracing.Span

public fun interface Tracer {
  public suspend fun trace(trace: List<Span<*>>)
}
