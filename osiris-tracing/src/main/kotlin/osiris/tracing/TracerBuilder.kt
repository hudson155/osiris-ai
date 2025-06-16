package osiris.tracing

public class TracerBuilder internal constructor() {
  private val listeners: MutableList<Listener> = mutableListOf()

  internal fun build(): Tracer =
    Tracer(listeners)

  public fun listener(listener: Listener) {
    listeners += listener
  }
}

public fun tracer(block: TracerBuilder.() -> Unit): Tracer =
  TracerBuilder().apply(block).build()
