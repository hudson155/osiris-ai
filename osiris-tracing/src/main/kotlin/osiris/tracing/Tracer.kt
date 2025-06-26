package osiris.tracing

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.uuid.Uuid
import kotlinx.coroutines.withContext

public class Tracer private constructor(
  internal val listeners: List<Listener>,
  internal val rootSpanId: Uuid?,
  internal val spanId: Uuid?,
) : AbstractCoroutineContextElement(Tracer), Listener {
  public constructor(listeners: List<Listener>) : this(
    listeners = listeners,
    rootSpanId = null,
    spanId = null,
  )

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

@Suppress("LongParameterList")
public suspend fun <T> withTracer(
  tracer: Tracer?,
  start: () -> TraceEvent.Start,
  end: (T?) -> TraceEvent.End,
  block: suspend () -> T,
): T {
  if (tracer == null || coroutineContext[Tracer] != null) return block()
  try {
    return withContext(tracer) {
      trace(start, end) {
        block()
      }
    }
  } finally {
    tracer.flush()
  }
}
