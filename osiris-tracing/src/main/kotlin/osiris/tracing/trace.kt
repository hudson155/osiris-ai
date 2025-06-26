package osiris.tracing

import java.time.Instant
import kotlin.coroutines.coroutineContext
import kotlin.uuid.Uuid
import kotlinx.coroutines.withContext

public typealias BuildStart = () -> Event.Details

public typealias BuildEnd<T> = (T) -> Event.Details

/**
 * Traces a span, emitting an event at both the start and the end.
 */
@Suppress("ForbiddenMethodCall")
public suspend fun <T> trace(
  /**
   * Creates the details for the start event, which is emitted immediately.
   */
  start: BuildStart,
  /**
   * Creates the details for the end event, which is emitted when [block] completes.
   * You can access [block]'s result. The result will be null if an exception was thrown.
   */
  end: BuildEnd<T?>,
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
  var result: T? = null
  return try {
    withContext(innerTraceContext) {
      result = block()
      return@withContext result
    }
  } finally {
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
  }
}
