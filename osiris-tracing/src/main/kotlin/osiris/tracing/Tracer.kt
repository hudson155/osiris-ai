package osiris.tracing

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
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

public suspend fun <T> withTracer(tracer: Tracer?, block: suspend () -> T): T {
  val nestedTracer = coroutineContext[Tracer].nested(tracer)
  val result = withContext(nestedTracer ?: EmptyCoroutineContext) {
    block()
  }
  if (nestedTracer != null && nestedTracer == tracer) nestedTracer.flush()
  return result
}

private fun Tracer?.nested(tracer: Tracer?): Tracer? {
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
