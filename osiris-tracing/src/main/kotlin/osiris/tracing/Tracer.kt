package osiris.tracing

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.uuid.Uuid

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

public fun Tracer?.nested(tracer: Tracer?): Tracer? {
  if (this == null) return tracer
  if (tracer == null) return this
  check(tracer.rootSpanId == null)
  check(tracer.spanId == null)
  return Tracer(
    listeners = listeners + tracer.listeners,
    rootSpanId = rootSpanId,
    spanId = spanId,
  )
}
