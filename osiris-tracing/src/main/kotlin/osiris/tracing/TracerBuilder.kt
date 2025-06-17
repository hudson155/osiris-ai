package osiris.tracing

public class TracerBuilder internal constructor() {
  private val listeners: MutableList<Listener> = mutableListOf()

  internal fun build(): Tracer =
    Tracer(listeners)

  public fun listener(listener: Listener) {
    listeners += listener
  }
}

/**
 * Helper DSL to build a [Tracer].
 */
public fun tracer(block: TracerBuilder.() -> Unit): Tracer =
  TracerBuilder().apply(block).build()
