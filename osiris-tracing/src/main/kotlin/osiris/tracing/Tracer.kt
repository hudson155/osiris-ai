package osiris.tracing

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.uuid.Uuid
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext

public class Tracer private constructor(
  internal val listeners: List<Listener>,
  internal val rootSpanId: Uuid?,
  internal val spanId: Uuid?,
) : AbstractCoroutineContextElement(Tracer), Listener {
  internal var level: TraceLevel = TraceLevel.Default

  public constructor(listeners: List<Listener>) : this(
    listeners = listeners,
    rootSpanId = null,
    spanId = null,
  )

  public override fun event(event: Event) {
    listeners.forEach { it.event(event) }
  }

  public fun escalate(level: TraceLevel) {
    if (level.ordinal > this.level.ordinal) {
      this.level = level
    }
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
  buildStart: BuildStart,
  buildEnd: BuildEnd<T>,
  block: suspend () -> T,
): T {
  if (tracer == null || currentCoroutineContext()[Tracer] != null) return block()
  try {
    return withContext(tracer) {
      trace(buildStart, buildEnd) {
        block()
      }
    }
  } finally {
    tracer.flush()
  }
}

public suspend fun getTracer(): Tracer? =
  currentCoroutineContext()[Tracer]
