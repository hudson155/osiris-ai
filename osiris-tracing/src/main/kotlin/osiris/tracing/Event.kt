package osiris.tracing

import java.time.Instant
import kotlin.uuid.Uuid

public data class Event(
  val spanId: Uuid,
  val parentSpanId: Uuid?,
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

  public abstract class Details
}
