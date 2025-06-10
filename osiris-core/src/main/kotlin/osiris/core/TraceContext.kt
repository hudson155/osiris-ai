package osiris.core

import java.time.Instant
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import osiris.span.Span

public class TraceContext : AbstractCoroutineContextElement(key) {
  public val spans: MutableList<Span<*>> = mutableListOf()

  public companion object {
    public val key: CoroutineContext.Key<TraceContext> =
      object : CoroutineContext.Key<TraceContext> {}
  }
}

public suspend fun getTraceContext(): TraceContext =
  checkNotNull(coroutineContext[TraceContext.key])

public suspend inline fun <T> trace(
  span: (T) -> Span.Details,
  block: () -> T,
): T {
  with(getTraceContext()) {
    val start = Instant.now()
    val result = block()
    val end = Instant.now()
    spans += Span(
      start = start,
      end = end,
      details = span(block()),
    )
    return result
  }
}
