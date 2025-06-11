package osiris.agentic

import osiris.span.Span

public fun interface Tracer {
  public suspend fun trace(trace: List<Span<*>>)
}
