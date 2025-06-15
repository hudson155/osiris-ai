package osiris.tracing

import java.time.Instant
import kotlin.coroutines.coroutineContext
import kotlin.uuid.Uuid
import kotlinx.coroutines.withContext

public typealias BuildStart = () -> Event.Details

public typealias BuildEnd<T> = (T) -> Event.Details

public suspend fun <T> trace(
  start: BuildStart,
  end: BuildEnd<T>,
  block: suspend () -> T,
): T {
  val outerTracer = coroutineContext[Tracer]
  if (outerTracer == null) {
    return block()
  }
  val startEvent = Event(
    spanId = Uuid.random(),
    parentSpanId = outerTracer.spanId,
    start = Event.Start(
      at = Instant.now(),
      details = start(),
    ),
    end = null,
  )
  outerTracer.send(startEvent)
  val innerTraceContext = outerTracer.withSpanId(startEvent.spanId)
  val result = withContext(innerTraceContext) {
    block()
  }
  val endEvent = Event(
    spanId = startEvent.spanId,
    parentSpanId = startEvent.parentSpanId,
    start = startEvent.start,
    end = Event.End(
      at = Instant.now(),
      details = end(result),
    )
  )
  outerTracer.send(endEvent)
  return result
}
