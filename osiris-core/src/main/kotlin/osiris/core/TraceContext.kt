package osiris.core

import java.time.Instant
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.uuid.Uuid
import kotlinx.coroutines.withContext
import osiris.span.Span

public class TraceContext private constructor(
  internal val spanId: Uuid? = null,
) : AbstractCoroutineContextElement(key) {
  public val spans: MutableList<Span<*>> = mutableListOf()

  internal fun withSpanId(spanId: Uuid): TraceContext =
    TraceContext(spanId)

  public companion object {
    public val key: CoroutineContext.Key<TraceContext> =
      object : CoroutineContext.Key<TraceContext> {}

    public suspend fun create(): TraceContext =
      coroutineContext[key] ?: TraceContext()
  }
}

public suspend fun getTraceContext(): TraceContext =
  checkNotNull(coroutineContext[TraceContext.key])

@Suppress("ForbiddenMethodCall")
public suspend fun <T> trace(
  span: (T) -> Span.Details,
  block: suspend () -> T,
): T {
  val outerTraceContext = getTraceContext()
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
    details = span(result)
  )
  return result
}
