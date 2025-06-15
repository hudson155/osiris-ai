package osiris.tracing

public class TracerBuilder internal constructor() {
  private val listeners: MutableList<Listener> = mutableListOf()

  internal fun build(): Tracer =
    Tracer(
      listeners = listeners,
      rootSpanId = null,
      spanId = null,
    )

  public fun listen(listener: Listener) {
    listeners += listener
  }
}

public fun tracer(block: TracerBuilder.() -> Unit): Tracer =
  TracerBuilder().apply(block).build()
