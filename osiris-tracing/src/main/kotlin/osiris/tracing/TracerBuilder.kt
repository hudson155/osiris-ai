package osiris.tracing

import kotlin.coroutines.coroutineContext

public class TracerBuilder internal constructor(
  private val listeners: MutableList<Listener>,
) {
  internal fun build(): Tracer =
    Tracer(
      listeners = listeners,
      spanId = null,
    )

  public fun listen(listener: Listener) {
    listeners += listener
  }

  public companion object {
    internal fun from(tracer: Tracer?): TracerBuilder =
      TracerBuilder(
        listeners = tracer?.listeners.orEmpty().toMutableList(),
      )
  }
}

public suspend fun tracer(block: TracerBuilder.() -> Unit): Tracer =
  TracerBuilder.from(coroutineContext[Tracer]).apply(block).build()
