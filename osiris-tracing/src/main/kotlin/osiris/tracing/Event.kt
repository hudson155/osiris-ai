package osiris.tracing

import java.time.Instant
import kotlin.uuid.Uuid

/**
 * For each span, an event is fired at the start and at the end.
 * The start event will only have [start] defined,
 * whereas the end event will have both [start] and [end] defined.
 *
 * Span IDs are managed automatically.
 */
public data class Event(
  val spanId: Uuid,
  val parentSpanId: Uuid?,
  val rootSpanId: Uuid,
  val start: Start<*>,
  val end: End<*>?,
) {
  public data class Start<T : Details>(
    val at: Instant,
    val details: T,
  )

  public data class End<T : Details>(
    val at: Instant,
    val details: T,
  )

  /**
   * Extend this class for custom events.
   */
  public abstract class Details
}
