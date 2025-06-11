package osiris.span

import java.time.Instant
import kotlin.uuid.Uuid

public data class Span<T : Span.Details>(
  val id: Uuid,
  val parentId: Uuid?,
  val start: Instant,
  val end: Instant,
  val details: T,
) {
  public abstract class Details
}
