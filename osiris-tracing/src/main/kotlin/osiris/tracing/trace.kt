package osiris.tracing

import java.time.Instant
import kotlin.coroutines.coroutineContext
import kotlin.uuid.Uuid
import kotlinx.coroutines.withContext

public typealias BuildStart = () -> Event.Details

public typealias BuildEnd<T> = (T) -> Event.Details

@Suppress("ForbiddenMethodCall")
public suspend fun <T> trace(
  start: BuildStart,
  end: BuildEnd<T>,
  block: suspend () -> T,
): T {
  val outerTracer = coroutineContext[Tracer]
  if (outerTracer == null) {
    return block()
  }
  val spanId = Uuid.random()
  val startEvent = Event(
    spanId = spanId,
    parentSpanId = outerTracer.spanId,
    rootSpanId = outerTracer.rootSpanId ?: spanId,
    start = Event.Start(
      at = Instant.now(),
      details = start(),
    ),
    end = null,
  )
  outerTracer.event(startEvent)
  val innerTraceContext = outerTracer.withSpanId(startEvent.spanId)
  val result = withContext(innerTraceContext) {
    block()
  }
  val endEvent = Event(
    spanId = startEvent.spanId,
    parentSpanId = startEvent.parentSpanId,
    rootSpanId = startEvent.rootSpanId,
    start = startEvent.start,
    end = Event.End(
      at = Instant.now(),
      details = end(result),
    ),
  )
  outerTracer.event(endEvent)
  return result
}
