package osiris

import io.ktor.util.Attributes
import kotlin.reflect.KMutableProperty1

/**
 * The [Agent]'s [Context] is available throughout the execution lifecycle.
 * For maximum flexibility, metadata is stored in coroutine-safe [Attributes]
 * instead of directly on the context itself.
 */
public class Context {
  public val attributes: Attributes = Attributes(concurrent = true)
}

/**
 * Creates a new [Context].
 */
public inline fun context(block: Context.() -> Unit): Context =
  Context().apply(block)

/**
 * Set the value of [property] for the duration of the [block].
 */
public inline fun <V, T> Context.with(
  property: KMutableProperty1<Context, V>,
  value: V,
  block: () -> T,
): T {
  val previousValue = property.get(this)
  property.set(this, value)
  try {
    return block()
  } finally {
    property.set(this, previousValue)
  }
}
