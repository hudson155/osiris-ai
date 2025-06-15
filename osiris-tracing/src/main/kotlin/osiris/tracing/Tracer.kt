package osiris.tracing

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.uuid.Uuid
import kotlinx.coroutines.withContext

public class Tracer internal constructor(
  internal val listeners: List<Listener>,
  internal val rootSpanId: Uuid?,
  internal val spanId: Uuid?,
) : AbstractCoroutineContextElement(Tracer), Listener {
  public override fun event(event: Event) {
    listeners.forEach { it.event(event) }
  }

  public override fun flush() {
    listeners.forEach { it.flush() }
  }

  internal fun withSpanId(spanId: Uuid): Tracer =
    Tracer(
      listeners = listeners,
      rootSpanId = rootSpanId ?: spanId,
      spanId = spanId,
    )

  public companion object : CoroutineContext.Key<Tracer>
}

public suspend fun <T> withTracer(
  tracer: Tracer?,
  start: () -> TraceEvent.Start,
  end: (T) -> TraceEvent.End,
  block: suspend () -> T,
): T {
  if (tracer == null || coroutineContext[Tracer] != null) return block()
  return withContext(tracer) {
    trace(start, end) {
      block()
    }
  }.also { tracer.flush() }
}
