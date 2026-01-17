package osiris

import io.ktor.util.Attributes

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
