package osiris.tracing

import java.time.Instant
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.uuid.Uuid
import kotlinx.coroutines.withContext

public class TraceContext(
  internal val spanId: Uuid? = null,
) : AbstractCoroutineContextElement(key) {
  internal val spans: MutableList<Span<*>> = CopyOnWriteArrayList()

  internal fun withSpanId(spanId: Uuid): TraceContext =
    TraceContext(spanId)

  public companion object {
    public val key: CoroutineContext.Key<TraceContext> =
      object : CoroutineContext.Key<TraceContext> {}
  }
}

public suspend fun <T> trace(block: suspend () -> T): Pair<T, Trace> {
  val traceContext = getTraceContext() ?: TraceContext()
  val response = withContext(traceContext) {
    block()
  }
  return Pair(response, Trace(traceContext.spans))
}

public suspend fun getTraceContext(): TraceContext? =
  coroutineContext[TraceContext.key]

@Suppress("ForbiddenMethodCall")
public suspend fun <T> trace(
  span: (T) -> Span.Details,
  block: suspend () -> T,
): T {
  val outerTraceContext = getTraceContext() ?: return block()
  val spanId = Uuid.random()
  val start = Instant.now()
  val innerTraceContext = outerTraceContext.withSpanId(spanId)
  val result = withContext(innerTraceContext) {
    block()
  }
  val end = Instant.now()
  outerTraceContext.spans += innerTraceContext.spans
  outerTraceContext.spans += Span(
    id = spanId,
    parentId = outerTraceContext.spanId,
    start = start,
    end = end,
    details = span(result),
  )
  return result
}
