package osiris.tracing

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.uuid.Uuid

public class Tracer internal constructor(
  internal val listeners: List<Listener>,
  internal val spanId: Uuid?,
) : AbstractCoroutineContextElement(Tracer) {
  public fun send(event: Event) {
    listeners.forEach { it.event(event) }
  }

  internal fun withSpanId(spanId: Uuid): Tracer =
    Tracer(
      listeners = listeners,
      spanId = spanId,
    )

  public companion object : CoroutineContext.Key<Tracer>
}
