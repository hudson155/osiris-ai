package osiris.tracing

import kotlin.uuid.Uuid
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext

/**
 * Traces a span, emitting an event at both the start and the end.
 */
@Suppress("ForbiddenMethodCall")
public suspend fun <T> trace(
  /**
   * Creates the details for the start event, which is emitted immediately.
   */
  buildStart: BuildStart,
  /**
   * Creates the details for the end event, which is emitted when [block] completes.
   * You can access [block]'s result. The result will be null if an exception was thrown.
   */
  buildEnd: BuildEnd<T>,
  block: suspend () -> T,
): T {
  val outerTracer = currentCoroutineContext()[Tracer]
  if (outerTracer == null) {
    return block()
  }
  val spanId = Uuid.random()
  val startEvent = buildStart().let { creator ->
    Event.Start.create(
      spanId = spanId,
      parentSpanId = outerTracer.spanId,
      rootSpanId = outerTracer.rootSpanId ?: spanId,
      creator = creator,
    )
  }
  outerTracer.event(startEvent)
  val innerTraceContext = outerTracer.withSpanId(startEvent.spanId)
  var endEvent: Event? = null
  return try {
    withContext(innerTraceContext) {
      val result = block()
      endEvent = buildEnd(result).let { Event.End.create(startEvent, innerTraceContext, it) }
      return@withContext result
    }
  } catch (e: Throwable) {
    if (endEvent == null) {
      endEvent = Event.End.exception(startEvent, e)
    }
    throw e
  } finally {
    outerTracer.event(checkNotNull(endEvent))
  }
}
