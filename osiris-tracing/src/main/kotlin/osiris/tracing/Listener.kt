package osiris.tracing

/**
 * Implement this interface to create custom Listeners.
 */
public interface Listener {
  /**
   * Called when there's a new event.
   * This is intentionally not a suspending function.
   * Do not do any expensive work directly.
   * If expensive work is required, outsource it to a separate coroutine.
   */
  public fun event(event: Event)

  /**
   * Called when there are no more events.
   */
  public fun flush()
}
