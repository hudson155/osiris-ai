package osiris.agentic

import osiris.tracing.Trace

public fun interface Tracer {
  public suspend fun trace(trace: Trace)
}
